package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.experimental.UtilityClass;
import pl.lukaskierzek.sushi.shop.service.GetProductResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartKafkaConsumer.CartItemPriceUpdatedEventDto;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

@UtilityClass
class CartMapper {

    static CartResponse toCartResponse(Cart cart) {
        return new CartResponse(cart.getItems().stream()
            .map(CartItemMapper::toCartItemResponse)
            .collect(toUnmodifiableSet()),
            cart.calculateTotalPrice());
    }

    static Map<String, Cart> toCartsWithItem(Set<String> userIds, Function<String, Optional<Cart>> cartMapper) {
        var result = new HashMap<String, Cart>();

        for (var userId : userIds) {
            cartMapper.apply(userId)
                .ifPresent(value -> result.put(userId, value));
        }

        return unmodifiableMap(result);
    }

    static Set<Cart> toCarts(CartItemPriceUpdatedEventDto event, Map<String, Cart> cartsWithItem) {
        var result = new HashSet<Cart>();

        for (var cartEntry : cartsWithItem.entrySet()) {
            var cart = cartEntry.getValue();
            var modifiableItems = CartItemMapper.toCartItems(cart, event);
            if (modifiableItems.isEmpty()) {
                continue;
            }

            cart.replaceItems(modifiableItems);
            result.add(cart);
        }

        if (result.isEmpty()) {
            return new HashSet<>();
        }

        return unmodifiableSet(result);
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

    static CartItem toCartItem(CartItemRequest cartItemRequest, GetProductResponse getProductResponse) {
        return new CartItem(cartItemRequest.id(), cartItemRequest.quantity(), MoneyMapper.toMoney(getProductResponse.getPrice()));
    }

    static Set<CartItem> toCartItems(Cart cart, CartItemPriceUpdatedEventDto event) {
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
}
