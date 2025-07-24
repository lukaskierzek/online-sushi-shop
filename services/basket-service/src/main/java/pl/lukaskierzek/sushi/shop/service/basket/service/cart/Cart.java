package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
class Cart {

    private final String id;
    private final Set<CartItem> items;

    static Cart newCart() {
        return new Cart(randomUUID().toString(), new HashSet<>());
    }

    void addItem(CartItem item) {
        items.add(item);
    }

    BigDecimal calculateTotal() {
        return items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

