package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemsRemovedEvent;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.*;

@Service
class CartService {

    private final CartRepository repository;
    private final ProductServiceBlockingStub productsStub;

    CartService(CartRepository repository, @GrpcClient("product") ProductServiceBlockingStub productsStub) {
        this.repository = repository;
        this.productsStub = productsStub;
    }

    @Transactional
    public String createCart(OwnerId ownerId) {
        var maybeCart = repository.getCart(ownerId);
        if (maybeCart.isPresent()) {
            throw new CartAlreadyExistsException("Cart for this user already exists");
        }

        var cart = Cart.newCart(ownerId);
        repository.saveCart(cart);
        return cart.getId();
    }

    CartResponse getCart(OwnerId ownerId) {
        var cart = getCartOrThrow(ownerId);
        return toCartResponse(cart);
    }

    @Transactional
    public void updateCart(OwnerId ownerId, CartItemsRequest request) {
        var newItems = request.items().stream()
            .map(this::toCartItem)
            .collect(toUnmodifiableSet());

        var cart = getCartOrThrow(ownerId);
        cart.replaceItems(newItems);

        repository.saveCart(cart);
    }

    @EventListener
    public void onCartItemAdded(CartItemAddedEvent event) {
        repository.saveProductOwnersIds(event.ownerId(), event.items());
    }

    @EventListener
    public void onCartItemsRemoved(CartItemsRemovedEvent event) {
        repository.deleteProductOwnersIds(event.ownerId(), event.productsIds());
    }

    private Cart getCartOrThrow(OwnerId ownerId) {
        return repository.getCart(ownerId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
    }

    private CartItem toCartItem(CartItemRequest cartItemRequest) {
        var product = productsStub.getProduct(GetProductRequest.newBuilder()
            .setId(cartItemRequest.id())
            .build());
        return CartItemMapper.toCartItem(cartItemRequest, product);
    }
}
