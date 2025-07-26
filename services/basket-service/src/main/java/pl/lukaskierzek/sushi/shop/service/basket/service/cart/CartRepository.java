package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Slf4j
@Repository
class CartRepository {

    private static final String CARTS_PREFIX = "carts::";
    private static final String PRODUCT_TO_USERS_PREFIX = "product-to-users::";

    private final RedisTemplate<Object, Object> redisTemplate;
    private final Duration ttl;
    private final ApplicationEventPublisher eventPublisher;

    CartRepository(RedisTemplate<Object, Object> redisTemplate, @Value("${spring.data.redis.caches.carts.ttl:PT24H}") Duration ttl, ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
        this.eventPublisher = eventPublisher;
    }

    Optional<Cart> getCart(String userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(buildCartKey(userId)))
            .map(Cart.class::cast);
    }

    void saveCart(Cart cart) {
        final var events = Set.copyOf(cart.getEvents());
        cart.clearEvents();

        redisTemplate.opsForValue().set(buildCartKey(cart.getUserId()), cart, ttl);

        log.info("Cart created {}{}", CARTS_PREFIX, cart.getId());

        events.forEach(eventPublisher::publishEvent);
    }

    void saveProductUsersIds(String userId, Set<CartItem> cartItems) {
        cartItems.forEach(cartItem -> {
            var ops = redisTemplate.boundSetOps(buildProductToUserKey(cartItem.getProductId()));
            ops.add(userId);
            ops.expire(ttl);

            log.info("User {} added to {} - {}", userId, PRODUCT_TO_USERS_PREFIX, cartItem.getProductId());
        });
    }

    Set<String> getProductUsersIds(String productId) {
        return Optional.ofNullable(redisTemplate.opsForSet().members(buildProductToUserKey(productId)))
            .map(objects -> objects.stream()
                .map(String.class::cast)
                .collect(toUnmodifiableSet()))
            .orElseGet(HashSet::new);
    }

    void deleteProductUsersIds(String userId, Set<String> productsIds) {
        productsIds.forEach(productId -> {
            var ops = redisTemplate.boundSetOps(buildProductToUserKey(productId));
            ops.remove(userId);

            log.info("User {} removed from {} - {}", userId, PRODUCT_TO_USERS_PREFIX, productId);
        });
    }

    private String buildCartKey(String userId) {
        return CARTS_PREFIX.concat(userId);
    }

    private String buildProductToUserKey(String productId) {
        return PRODUCT_TO_USERS_PREFIX.concat(productId);
    }
}
