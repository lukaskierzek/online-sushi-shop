package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
class CartValidator {

    static String validateProductId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidCartException("Product ID cannot be null or empty");
        }
        return id;
    }

    static Integer validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidCartException("Quantity must be greater than 0");
        }
        return quantity;
    }

    static String validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidCartException("User ID cannot be null or empty");
        }
        return userId;
    }

    static CartItem validateCartItem(Set<CartItem> items, CartItem item) {
        var maybeExistingItem = items.stream()
            .filter(i -> i.getId().equals(item.getId()))
            .findAny();
        if (maybeExistingItem.isPresent()) {
            throw new InvalidCartItemException("Cart item already added");
        }

        return item;
    }
}
