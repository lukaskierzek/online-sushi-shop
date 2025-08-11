package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
class CartKafkaConsumer {

    private final ObjectMapper mapper;
    private final CartRepository repository;
    private final TransactionTemplate transactionTemplate;

    @KafkaListener(topics = "${kafka.topics.product-price-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCartItemPriceUpdated(String payload) {
        CartItemPriceUpdatedEventDto event;
        try {
            event = mapper.readValue(payload, CartItemPriceUpdatedEventDto.class);
        } catch (JsonProcessingException e) {
            log.error("Cannot convert json to CartItemPriceUpdatedEventDto", e);

            throw new CartItemPriceProcessingException("Cannot convert json: " + payload + " to CartItemPriceUpdatedEventDto");
        }

        var ownerIds = repository.getProductOwnersIds(event.id());
        if (CollectionUtils.isEmpty(ownerIds)) {
            return;
        }

        transactionTemplate.executeWithoutResult(tx -> {
            for (var ownerId : ownerIds) {

                var cart = repository.getCart(ownerId)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found"));

                cart.updateCartItemPrice(event.id(), event.price());

                repository.saveCart(cart);
            }
        });
    }


    record CartItemPriceUpdatedEventDto(String id, Money price) {
    }
}
