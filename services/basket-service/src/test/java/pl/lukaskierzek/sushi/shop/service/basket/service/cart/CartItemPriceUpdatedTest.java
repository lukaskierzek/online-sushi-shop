package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import pl.lukaskierzek.sushi.shop.service.basket.service.IntegrationTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

class CartItemPriceUpdatedTest extends IntegrationTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${kafka.topics.product-price-updated}")
    String productPriceUpdatedTopic;

    @MockitoSpyBean
    CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        kafkaTemplate.flush();
    }

    @Test
    void shouldUpdateCartItemPriceViaKafka() throws Exception {
        // given
        var ownerId = new OwnerId(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        var productId = UUID.randomUUID().toString();

        var cart = Cart.newCart(ownerId);
        cart.addItem(new CartItem(productId, 1, new Money(Currency.PLN, new BigDecimal("10.00"))));
        redisTemplate.opsForValue().set(redisCartKey(ownerId), cart);

        redisTemplate.boundSetOps(redisProductKey(productId)).add(ownerId);

        var updatedPrice = new Money(Currency.EUR, new BigDecimal("20.00"));
        var kafkaEvent = new CartKafkaConsumer.CartItemPriceUpdatedEventDto(productId, updatedPrice);

        // when
        kafkaTemplate.send(productPriceUpdatedTopic, objectMapper.writeValueAsString(kafkaEvent));

        // then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Cart updatedCart = (Cart) redisTemplate.opsForValue().get(redisCartKey(ownerId));
            assertNotNull(updatedCart);

            var updatedItem = updatedCart.getItems().iterator().next();
            assertThat(updatedItem.unitPrice()).isEqualTo(updatedPrice);

            Set<Object> owners = redisTemplate.opsForSet().members(redisProductKey(productId));
            assertThat(owners).containsExactly(ownerId);
        });
    }

    @Test
    void shouldRollbackOnRepositoryErrorAndPreserveOldCartState() throws Exception {
        // given
        var ownerId = new OwnerId(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        var productId = UUID.randomUUID().toString();

        var originalPrice = new Money(Currency.PLN, new BigDecimal("10.00"));
        var originalItem = new CartItem(productId, 1, originalPrice);

        var cart = Cart.newCart(ownerId);
        cart.addItem(originalItem);
        redisTemplate.opsForValue().set(redisCartKey(ownerId), cart);
        redisTemplate.boundSetOps(redisProductKey(productId)).add(ownerId);

        // simulate failure
        doThrow(new RuntimeException("Simulated failure"))
            .when(cartRepository)
            .saveCart(any());

        var updatedPrice = new Money(Currency.EUR, new BigDecimal("20.00"));
        var kafkaEvent = new CartKafkaConsumer.CartItemPriceUpdatedEventDto(productId, updatedPrice);

        // when
        kafkaTemplate.send(productPriceUpdatedTopic, objectMapper.writeValueAsString(kafkaEvent));

        // then
        Thread.sleep(5000);

        Cart unchangedCart = (Cart) redisTemplate.opsForValue().get(redisCartKey(ownerId));
        assertNotNull(unchangedCart);

        var item = unchangedCart.getItems().iterator().next();
        assertThat(item.unitPrice()).isEqualTo(originalPrice);

        Set<Object> owners = redisTemplate.opsForSet().members(redisProductKey(productId));
        assertThat(owners).containsExactly(ownerId);
    }

    private String redisCartKey(OwnerId ownerId) {
        return "carts::" + ownerId;
    }

    private String redisProductKey(String productId) {
        return "product-to-owners::" + productId;
    }
}

