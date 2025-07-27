package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemsRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemAddedEvent;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.DomainEvent.CartItemsRemovedEvent;

import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartMapper.*;

@Service
class CartService {

    private final CartRepository repository;
    private final ProductServiceBlockingStub productsStub;
    private final ObjectMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    CartService(CartRepository repository, @GrpcClient("product") ProductServiceBlockingStub productsStub, ObjectMapper mapper, ApplicationEventPublisher eventPublisher, TransactionTemplate transactionTemplate) {
        this.repository = repository;
        this.productsStub = productsStub;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = transactionTemplate;
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

    @KafkaListener(topics = "${kafka.topics.product-price-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCartItemPriceUpdated(String payload) throws JsonProcessingException {
        var event = mapper.readValue(payload, CartItemPriceUpdated.class);

        var userIds = repository.getProductUsersIds(event.id());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        var cartsWithItem = toCartsWithItem(userIds, repository::getCart);

        var carts = toCarts(event, cartsWithItem);
        if (carts.isEmpty()) {
            return;
        }

        transactionTemplate.executeWithoutResult(status ->
            carts.forEach(cart -> {
                final var events = Set.copyOf(cart.getEvents());
                cart.clearEvents();

                repository.saveCart(cart);
                events.forEach(eventPublisher::publishEvent);
            }));
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
