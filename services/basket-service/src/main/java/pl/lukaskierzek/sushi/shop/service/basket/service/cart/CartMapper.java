package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;
import pl.lukaskierzek.sushi.shop.service.GetProductResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartService.CartItemPriceUpdated;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
class CartMapper {

    static CartResponse toCartResponse(Cart cart) {
        return new CartResponse(cart.getItems().stream()
            .map(CartMapper::toCartItemResponse)
            .collect(toUnmodifiableSet()),
            cart.calculateTotalPrice());
    }

    static CartItem toCartItem(CartItemRequest cartItemRequest, GetProductResponse getProductResponse) {
        return new CartItem(cartItemRequest.id(), cartItemRequest.quantity(), toMoney(getProductResponse.getPrice()));
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

    static Set<Cart> toCarts(CartItemPriceUpdated event, Map<String, Cart> cartsWithItem) {
        var result = new HashSet<Cart>();

        for (var cartEntry : cartsWithItem.entrySet()) {
            var cart = cartEntry.getValue();
            var modifiableItems = toCartItems(cart, event);
            if (modifiableItems.isEmpty()) {
                continue;
            }

            cart.replaceItems(modifiableItems);
            result.add(cart);
        }

        if (result.isEmpty()) {
            return new HashSet<>();
        }

        return result;
    }

    private Set<CartItem> toCartItems(Cart cart, CartItemPriceUpdated event) {
        var items = cart.getItems();

        var modifiableItems = new HashSet<CartItem>();
        var nonModifiableItems = new HashSet<CartItem>();

        for (var item : items) {
            if (item.productId().equals(event.id()) && !Objects.equals(item.unitPrice(), event.price())) {
                modifiableItems.add(new CartItem(item.productId(), item.quantity(), event.price()));
                continue;
            }
            nonModifiableItems.add(item);
        }

        if (modifiableItems.isEmpty()) {
            return new HashSet<>();
        }

        return Stream.concat(nonModifiableItems.stream(), modifiableItems.stream())
            .collect(toUnmodifiableSet());
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(cartItem.productId(), cartItem.quantity(), cartItem.unitPrice());
    }
}
