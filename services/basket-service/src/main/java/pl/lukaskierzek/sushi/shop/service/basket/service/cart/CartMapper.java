package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;
import pl.lukaskierzek.sushi.shop.service.GetProductResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
class CartMapper {

    static CartResponse toCartResponse(Cart cart) {
        return new CartResponse(cart.getItems().stream()
            .map(CartMapper::toCartItemResponse)
            .collect(toUnmodifiableSet()),
            cart.calculateTotal());
    }

    static CartItem toCartItem(CartItemRequest cartItemRequest, GetProductResponse getProductResponse) {
        return CartItem.of(cartItemRequest.id(), cartItemRequest.quantity(), toMoney(getProductResponse.getPrice()));
    }

    static Money toMoney(pl.lukaskierzek.sushi.shop.service.Money price) {
        return new Money(
            Currency.valueOf(price.getCurrency().name()),
            new BigDecimal(price.getAmount()));
    }

    static Map<String, Cart> toCartsWithItem(Set<String> userIds, Function<String, Optional<Cart>> cartMapper) {
        var result = new HashMap<String, Cart>();

        for (var userId : userIds) {
            cartMapper.apply(userId)
                .ifPresent(value -> result.put(userId, value));
        }

        return unmodifiableMap(result);
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getPrice());
    }
}
