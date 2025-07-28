package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import java.io.Serializable;
import java.util.Set;

interface DomainEvent extends Serializable {

    record CartItemAddedEvent(String userId, Set<CartItem> items) implements DomainEvent {
    }

    record CartItemsRemovedEvent(String userId, Set<String> productsIds) implements DomainEvent {
    }
}
