package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

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

    static BigDecimal validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCartException("Price must be greater than 0");
        }
        return price;
    }
}
