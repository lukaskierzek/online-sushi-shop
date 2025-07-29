package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import java.io.Serializable;

interface DomainEvent extends Serializable {

    record CartItemAddedEvent(OwnerId ownerId, String productId) implements DomainEvent {
    }

    record CartItemRemovedEvent(OwnerId ownerId, String productId) implements DomainEvent {
    }
}
