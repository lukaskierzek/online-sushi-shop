package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import pl.lukaskierzek.sushi.shop.service.basket.service.IntegrationTest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartKafkaConsumer.CartItemPriceUpdatedEventDto;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CartItemPriceUpdatedTest extends IntegrationTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper mapper;

    @Value("${kafka.topics.product-price-updated}")
    String productPriceUpdatedTopic;

    @BeforeEach
    void setup() {
        assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void shouldUpdateCartItemPriceViaKafka() throws Exception {
        final var userId = UUID.randomUUID().toString();
        final var productId = UUID.randomUUID().toString();

        var cart = Cart.newCart(userId);
        cart.addItem(new CartItem(productId, 1, new Money(Currency.PLN, new BigDecimal("10.00"))));
        redisTemplate.opsForValue().set("carts::" + userId, cart);

        cart.getItems().forEach(cartItem -> {
            var ops = redisTemplate.boundSetOps("product-to-users::" + cartItem.productId());
            ops.add(userId);
        });

        var newPrice = new Money(Currency.EUR, new BigDecimal("20.00"));
        var event = new CartItemPriceUpdatedEventDto(productId, newPrice);

        kafkaTemplate.send(productPriceUpdatedTopic, mapper.writeValueAsString(event));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> {
                var updatedCart = (Cart) redisTemplate.opsForValue().get("carts::" + userId);
                assertNotNull(updatedCart);
                var item = updatedCart.getItems().iterator().next();
                assertThat(item.unitPrice().amount()).isEqualTo(newPrice.amount());
                assertThat(item.unitPrice().currency()).isEqualTo(newPrice.currency());

                var productToUsersMembers = redisTemplate.opsForSet().members("product-to-users::" + item.productId());
                assertNotNull(productToUsersMembers);
                assertThat(productToUsersMembers.size()).isEqualTo(1);
                assertThat(productToUsersMembers.iterator().next()).isEqualTo(userId);
            });
    }
}
