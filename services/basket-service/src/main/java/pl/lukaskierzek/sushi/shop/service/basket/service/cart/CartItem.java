package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartValidator.*;

@Getter
@AllArgsConstructor(access = PRIVATE)
class CartItem {

    private String id;
    private Integer quantity;
    private BigDecimal price;

    static CartItem of(String productId, Integer quantity, BigDecimal price) {
        return new CartItem(
            validateProductId(productId),
            validateQuantity(quantity),
            validatePrice(price)
        );
    }
}
