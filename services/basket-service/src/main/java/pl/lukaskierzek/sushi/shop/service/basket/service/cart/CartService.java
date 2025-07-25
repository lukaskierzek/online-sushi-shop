package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemsRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCartResponse;

@Service
class CartService {

    private static final BiPredicate<CartItem, String> CART_ITEM_PREDICATE =
        (cartItem, productId) -> cartItem.getProductId().equals(productId);

    private final CartRepository repository;
    private final ProductServiceBlockingStub productsStub;

    CartService(CartRepository repository, ProductServiceBlockingStub productsStub) {
        this.repository = repository;
        this.productsStub = productsStub;
    }

    String createCart(String userId) {
        var maybeCart = repository.getCart(userId);
        if (maybeCart.isPresent()) {
            throw new CartAlreadyExistsException("Cart for this user already exists");
        }

        var cart = Cart.newCart(userId);
        repository.saveCart(cart);
        return cart.getId();
    }

    CartResponse getCart(String userId) {
        var cart = getCartOrThrow(userId);
        return toCartResponse(cart);
    }

    @Transactional
    public void updateCart(String userId, CartItemsRequest request) {
        var cart = getCartOrThrow(userId);

        var changed = new AtomicBoolean(false);

        request.items().stream()
            .map(this::toCartItem)
            .forEach(cartItem -> {
                cart.addItem(cartItem);
                changed.set(true);
            });

        if (changed.get()) {
            repository.saveCart(cart);
            repository.saveProductUsersIds(userId, cart.getItems());
        }
    }

    @Transactional
    @KafkaListener(topics = "pl.lukaskierzek.catalog.product.price-updated", groupId = "basket-service")
    public void onCartItemPriceUpdated(CartItemPriceUpdated event) {
        var userIds = repository.getProductUsersIds(event.id());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        var cartsWithItem = CartMapper.toCartsWithItem(userIds, repository::getCart);

        for (var cartEntry : cartsWithItem.entrySet()) {
            var cart = cartEntry.getValue();

            var items = cart.getItems();

            var nonModifiableItems = items.stream()
                .filter(i -> !CART_ITEM_PREDICATE.test(i, event.id()));

            var modifiableItems = items.stream()
                .filter(i -> CART_ITEM_PREDICATE.test(i, event.id()))
                .map(i -> CartItem.of(i.getProductId(), i.getQuantity(), event.price()));

            var newItems = Stream.concat(nonModifiableItems, modifiableItems)
                .collect(toUnmodifiableSet());

            cart.replaceItems(newItems);

            repository.saveCart(cart);
        }
    }

    private Cart getCartOrThrow(String userId) {
        return repository.getCart(userId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
    }

    private CartItem toCartItem(CartItemRequest cartItemRequest) {
        var product = productsStub.getProduct(GetProductRequest.newBuilder()
            .setId(cartItemRequest.id())
            .build());
        return CartMapper.toCartItem(cartItemRequest, product);
    }

    record CartItemPriceUpdated(String id, Money price) {
    }
}
