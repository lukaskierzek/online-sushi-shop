package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemRemovedEvent;

import java.util.Objects;
import java.util.stream.Collectors;

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
        var cart = getCartOrThrow(ownerId);
        var existingItems = cart.getItems().stream()
            .collect(Collectors.toMap(CartItem::productId, item -> item));

        var incomingItems = request.items().stream()
            .collect(Collectors.toMap(CartItemRequest::id, item -> item));

        for (var existingItem : existingItems.values()) {
            if (!incomingItems.containsKey(existingItem.productId())) {
                cart.removeCartItem(existingItem.productId());
            }
        }

        for (var requestItem : request.items()) {
            var productId = requestItem.id();
            var quantity = requestItem.quantity();

            var product = productsStub.getProduct(
                GetProductRequest.newBuilder().setId(productId).build());

            var unitPrice = MoneyMapper.toMoney(product.getPrice());

            if (!existingItems.containsKey(productId)) {
                cart.addCartItem(productId, quantity, unitPrice);
                continue;
            }

            var existing = existingItems.get(productId);
            if (!Objects.equals(existing.quantity(), quantity)) {
                cart.updateCartItemQuantity(productId, quantity);
            }
        }

        repository.saveCart(cart);
    }


    @EventListener
    public void onCartItemAdded(CartItemAddedEvent event) {
        repository.saveProductOwnersIds(event.ownerId(), event.productId());
    }

    @EventListener
    public void onCartItemsRemoved(CartItemRemovedEvent event) {
        repository.deleteProductOwnersIds(event.ownerId(), event.productId());
    }

    private Cart getCartOrThrow(OwnerId ownerId) {
        return repository.getCart(ownerId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found"));
    }
}
