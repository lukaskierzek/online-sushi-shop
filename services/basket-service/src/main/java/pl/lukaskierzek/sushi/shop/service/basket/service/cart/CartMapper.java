package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;
import pl.lukaskierzek.sushi.shop.service.GetProductResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
class CartMapper {

    CartResponse toCartResponse(Cart cart) {
        return new CartResponse(cart.getItems().stream()
            .map(CartMapper::toCartItemResponse)
            .collect(toUnmodifiableSet()),
            cart.calculateTotal());
    }

    CartItem toCartItem(CartItemRequest cartItemRequest, GetProductResponse getProductResponse) {
        return CartItem.of(cartItemRequest.id(), cartItemRequest.quantity(), toMoney(getProductResponse.getPrice()));
    }

    Money toMoney(pl.lukaskierzek.sushi.shop.service.Money price) {
        return new Money(
            Currency.valueOf(price.getCurrency().name()),
            new BigDecimal(price.getAmount()));
    }

    Map<String, Cart> toCartsWithItem(Set<String> userIds, Function<String, Optional<Cart>> cartMapper) {
        return userIds.stream()
            .map(userId -> Map.entry(userId, cartMapper.apply(userId)))
            .filter(entry -> entry.getValue().isPresent())
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().get()))
            .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(cartItem.getProductId(), cartItem.getQuantity(), cartItem.getPrice());
    }
}
