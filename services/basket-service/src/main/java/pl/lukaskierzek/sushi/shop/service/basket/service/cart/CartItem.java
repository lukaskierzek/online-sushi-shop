package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
class CartItem {

    private final String productId;
    private final Integer quantity;
    private final Money price;

    static CartItem of(String productId, Integer quantity, Money price) {
        validateProductId(productId);
        validateQuantity(quantity);
        validatePrice(price);
        return new CartItem(productId, quantity, price);
    }

    Money calculatePrice() {
        return new Money(price.currency(), price.amount().multiply(BigDecimal.valueOf(quantity)));
    }

    private static void validateProductId(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new InvalidCartException("Product ID cannot be null or empty");
        }
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new InvalidCartException("Quantity must be greater than 0");
        }
    }

    private static void validatePrice(Money price) {
        if (price == null) {
            throw new InvalidCartException("Price cannot be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem other)) return false;
        return Objects.equals(productId, other.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
