package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCarts;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCartsWithItem;

@Component
@RequiredArgsConstructor
class CartKafkaConsumer {

    private final ObjectMapper mapper;
    private final CartRepository repository;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = "${kafka.topics.product-price-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCartItemPriceUpdated(String payload) throws JsonProcessingException {
        var event = mapper.readValue(payload, CartItemPriceUpdatedEventDto.class);

        var userIds = repository.getProductUsersIds(event.id());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        var cartsWithItem = toCartsWithItem(userIds, repository::getCart);

        var carts = toCarts(event, cartsWithItem);
        if (carts.isEmpty()) {
            return;
        }

        transactionTemplate.executeWithoutResult(status ->
            carts.forEach(cart -> {
                final var events = Set.copyOf(cart.getEvents());
                cart.clearEvents();

                repository.saveCart(cart);
                events.forEach(eventPublisher::publishEvent);
            }));
    }

    record CartItemPriceUpdatedEventDto(String id, Money price) {
    }
}
