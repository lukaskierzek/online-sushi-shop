package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemRemovedEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.unmodifiableSet;
import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@AllArgsConstructor(access = PRIVATE)
class Cart implements Serializable {

    @Getter
    private final String id;

    @Getter
    private final OwnerId ownerId;

    private final Set<CartItem> items;
    private final Set<DomainEvent> events;

    static Cart newCart(OwnerId ownerId) {
        validateOwnerId(ownerId);
        return new Cart(randomUUID().toString(), ownerId, new HashSet<>(), new HashSet<>());
    }

    Set<CartItem> getItems() {
        return unmodifiableSet(items);
    }

    Set<DomainEvent> getEvents() {
        return unmodifiableSet(events);
    }

    void clearEvents() {
        events.clear();
    }

    Money calculateTotalPrice() {
        return items.stream()
            .map(CartItem::calculatePrice)
            .reduce(Money::add)
            .orElse(new Money(Currency.PLN, BigDecimal.ZERO));
    }

    void addCartItem(String productId, int quantity, Money unitPrice) {
        var item = new CartItem(productId, quantity, unitPrice);
        items.add(validateNoDuplicate(item));
        events.add(new CartItemAddedEvent(ownerId, item.productId()));
    }

    void removeCartItem(String productId) {
        var removed = items.removeIf(cartItem -> cartItem.productId().equals(productId));
        if (!removed) {
            throw new InvalidCartItemException("Cart item does not exist");
        }
        events.add(new CartItemRemovedEvent(ownerId, productId));
    }

    void updateCartItemQuantity(String productId, int newQuantity) {
        CartItem existingItem = items.stream()
            .filter(item -> item.productId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new InvalidCartItemException("Cart item does not exist"));

        CartItem updatedItem = new CartItem(
            existingItem.productId(),
            newQuantity,
            existingItem.unitPrice());

        items.remove(existingItem);
        items.add(updatedItem);
    }

    void updateCartItemPrice(String productId, Money newPrice) {
        var existingItem = items.stream()
            .filter(i -> i.productId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new InvalidCartItemException("Cart item does not exist"));

        var updatedItem = new CartItem(productId, existingItem.quantity(), newPrice);

        items.remove(existingItem);
        items.add(updatedItem);
    }

    private CartItem validateNoDuplicate(CartItem item) {
        if (items.contains(item)) {
            throw new InvalidCartItemException("Cart item already added");
        }
        return item;
    }

    private static void validateOwnerId(OwnerId ownerId) {
        if (ownerId == null) {
            throw new InvalidCartException("Owner ID cannot be null");
        }
    }
}
