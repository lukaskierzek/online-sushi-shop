package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.lukaskierzek.sushi.shop.service.GetProductRequest;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceBlockingStub;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemResponse;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartItemsRequest;
import pl.lukaskierzek.sushi.shop.service.basket.service.cart.CartController.CartResponse;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
class CartService {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final ProductServiceBlockingStub productsStub;

    CartService(RedisTemplate<Object, Object> redisTemplate, ProductServiceBlockingStub productsStub) {
        this.redisTemplate = redisTemplate;
        this.productsStub = productsStub;
    }

    String createCart(String userId) {
        var cart = (Cart) redisTemplate.opsForValue().get("carts::" + userId);
        if (cart != null) {
            throw new CartAlreadyExistsException("Cart for this user already exists");
        }

        cart = Cart.newCart(userId);
        redisTemplate.opsForValue().set("carts::" + userId, cart);
        return cart.getId();
    }

    CartResponse getCart(String userId) {
        var cart = getCartOrThrow(userId);

        return new CartResponse(cart.getItems().stream()
            .map(cartItem -> new CartItemResponse(cartItem.getId(), cartItem.getQuantity(), cartItem.getPrice()))
            .collect(toUnmodifiableSet()),
            cart.calculateTotal());
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
            redisTemplate.opsForValue().set("carts::" + userId, cart);

            cart.getItems()
                .forEach(cartItem -> redisTemplate.opsForSet().add("product-to-users::" + cartItem.getId(), userId));
        }
    }

    @Transactional
    @KafkaListener(topics = "pl.lukaskierzek.catalog.product.price-updated", groupId = "basket-service")
    public void onCartItemPriceUpdated(CartItemPriceUpdated event) {
        String productId = event.id();
        Money newPrice = event.price();

        Set<Object> userIds = redisTemplate.opsForSet().members("product-to-users::" + productId);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        //TODO: end the method
    }

    private Cart getCartOrThrow(String userId) {
        var cart = (Cart) redisTemplate.opsForValue().get("carts::" + userId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }
        return cart;
    }

    private CartItem toCartItem(CartItemRequest cartItemRequest) {
        var product = productsStub.getProduct(GetProductRequest.newBuilder()
            .setId(cartItemRequest.id())
            .build());
        return CartItem.of(cartItemRequest.id(), cartItemRequest.quantity(), toMoney(product.getPrice()));
    }

    private Money toMoney(pl.lukaskierzek.sushi.shop.service.Money price) {
        return new Money(
            Currency.valueOf(price.getCurrency().name()),
            new BigDecimal(price.getAmount()));
    }

    record CartItemPriceUpdated(String id, Money price) {
    }
}
