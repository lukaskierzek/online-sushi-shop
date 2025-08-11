package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
class CartMapper {

    static CartResponse toCartResponse(Cart cart) {
        return new CartResponse(cart.getItems().stream()
            .map(CartItemMapper::toCartItemResponse)
            .collect(toUnmodifiableSet()),
            cart.calculateTotalPrice());
    }
}

@UtilityClass
class MoneyMapper {

    static Money toMoney(pl.lukaskierzek.sushi.shop.service.Money price) {
        return new Money(
            Currency.valueOf(price.getCurrency().name()),
            new BigDecimal(price.getAmount()));
    }
}

@UtilityClass
class CartItemMapper {

    static CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(cartItem.productId(), cartItem.quantity(), cartItem.unitPrice());
    }
}
