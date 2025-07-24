package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
@RequiredArgsConstructor
class CartService {

    private final CartRepository repository;

    String createCart(String userId) {
        var cart = repository.getCart(userId);
        if (cart != null) {
            throw new CartAlreadyExistsException("Cart for this user already exists");
        }

        return repository.setCart(Cart.newCart(userId)).getId();
    }

    CartResponse getCart(String userId) {
        var cart = repository.getCart(userId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }

        return new CartResponse(cart.getItems().stream()
            .map(cartItem -> new CartItemResponse(cartItem.getId(), cartItem.getQuantity(), cartItem.getPrice()))
            .collect(toUnmodifiableSet()),
            cart.calculateTotal());
    }

//    @KafkaListener
    public void onCartItemPriceUpdated(CartItemPriceUpdated event) {

    }

    record CartItemPriceUpdated(String id, Money price) {
    }
}
