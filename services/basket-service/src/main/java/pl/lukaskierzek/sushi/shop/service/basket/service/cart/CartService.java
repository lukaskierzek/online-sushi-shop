package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemsRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCartResponse;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.toCartsWithItem;

@Service
class CartService {

    private final CartRepository repository;
    private final ProductServiceBlockingStub productsStub;
    private final ObjectMapper mapper;

    CartService(CartRepository repository, @GrpcClient("product") ProductServiceBlockingStub productsStub, ObjectMapper mapper) {
        this.repository = repository;
        this.productsStub = productsStub;
        this.mapper = mapper;
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
    @KafkaListener(topics = "${kafka.topics.product-price-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCartItemPriceUpdated(String payload) throws JsonProcessingException {
        var event = mapper.readValue(payload, CartItemPriceUpdated.class);

        var userIds = repository.getProductUsersIds(event.id());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        var cartsWithItem = toCartsWithItem(userIds, repository::getCart);

        for (var cartEntry : cartsWithItem.entrySet()) {
            var changed = new AtomicBoolean(false);

            var cart = cartEntry.getValue();

            var items = cart.getItems();

            var modifiableItems = new HashSet<CartItem>();
            var nonModifiableItems = new HashSet<CartItem>();

            for (var item : items) {
                if (item.getProductId().equals(event.id()) && !Objects.equals(item.getPrice(), event.price())) {
                    changed.set(true);
                    modifiableItems.add(CartItem.of(item.getProductId(), item.getQuantity(), event.price()));
                    continue;
                }
                nonModifiableItems.add(item);
            }

            if (!changed.get()) {
                return;
            }

            var newItems = Stream.concat(nonModifiableItems.stream(), modifiableItems.stream())
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
