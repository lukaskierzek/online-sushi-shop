package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemsRemovedEvent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        return new Cart(randomUUID().toString(), ownerId, new HashSet<>(), new LinkedHashSet<>());
    }

    void addItem(CartItem item) {
        items.add(validateNoDuplicate(item));
        events.add(new CartItemAddedEvent(ownerId, items));
    }

    void replaceItems(Set<CartItem> newItems) {
        events.add(new CartItemsRemovedEvent(ownerId, items.stream()
            .map(CartItem::productId)
            .collect(Collectors.toUnmodifiableSet())));
        items.clear();
        newItems.forEach(this::addItem);
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

    private CartItem validateNoDuplicate(CartItem item) {
        if (items.stream().anyMatch(i -> i.productId().equals(item.productId()))) {
            throw new InvalidCartItemException("Cart item already added");
        }
        return item;
    }

    private static void validateOwnerId(OwnerId ownerId) {
        if (ownerId == null) {
            throw new InvalidCartException("Owner ID cannot be null or empty");
        }
    }
}
