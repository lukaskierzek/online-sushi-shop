package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemsRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemsRemovedEvent;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCartResponse;

@Service
class CartService {

    private final CartRepository repository;
    private final ProductServiceBlockingStub productsStub;

    CartService(CartRepository repository, @GrpcClient("product") ProductServiceBlockingStub productsStub) {
        this.repository = repository;
        this.productsStub = productsStub;
    }

    @Transactional
    public String createCart(String userId) {
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
        var newItems = request.items().stream()
            .map(this::toCartItem)
            .collect(toUnmodifiableSet());

        var cart = getCartOrThrow(userId);
        cart.replaceItems(newItems);

        repository.saveCart(cart);
    }

    @EventListener
    public void onCartItemAdded(CartItemAddedEvent event) {
        repository.saveProductUsersIds(event.userId(), event.items());
    }

    @EventListener
    public void onCartItemsRemoved(CartItemsRemovedEvent event) {
        repository.deleteProductUsersIds(event.userId(), event.productsIds());
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
