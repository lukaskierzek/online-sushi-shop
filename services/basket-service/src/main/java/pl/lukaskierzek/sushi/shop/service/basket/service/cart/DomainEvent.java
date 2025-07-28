package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import java.io.Serializable;
import java.util.Set;

interface DomainEvent extends Serializable {

    record CartItemAddedEvent(OwnerId ownerId, Set<CartItem> items) implements DomainEvent {
    }

    record CartItemsRemovedEvent(OwnerId ownerId, Set<String> productsIds) implements DomainEvent {
    }
}
