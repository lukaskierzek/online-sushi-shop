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
    private static final String PRODUCT_TO_OWNERS_PREFIX = "product-to-owners::";

    private final RedisTemplate<Object, Object> redisTemplate;
    private final Duration ttl;
    private final ApplicationEventPublisher eventPublisher;

    CartRepository(RedisTemplate<Object, Object> redisTemplate, @Value("${spring.data.redis.caches.carts.ttl:PT24H}") Duration ttl, ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
        this.eventPublisher = eventPublisher;
    }

    Optional<Cart> getCart(OwnerId ownerId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(buildCartKey(ownerId)))
            .map(Cart.class::cast);
    }

    void saveCart(Cart cart) {
        final var events = Set.copyOf(cart.getEvents());
        cart.clearEvents();

        redisTemplate.opsForValue().set(buildCartKey(cart.getOwnerId()), cart, ttl);

        log.info("Cart created {}{}", CARTS_PREFIX, cart.getId());

        events.forEach(eventPublisher::publishEvent);
    }

    void saveProductOwnersIds(OwnerId ownerId, String productId) {
        var ops = redisTemplate.boundSetOps(buildProductToOwnerKey(productId));
        ops.add(ownerId);
        ops.expire(ttl);

        log.info("Owner {} added to {} - {}", ownerId, PRODUCT_TO_OWNERS_PREFIX, productId);
    }

    Set<OwnerId> getProductOwnersIds(String productId) {
        return Optional.ofNullable(redisTemplate.opsForSet().members(buildProductToOwnerKey(productId)))
            .map(objects -> objects.stream()
                .map(OwnerId.class::cast)
                .collect(toUnmodifiableSet()))
            .orElseGet(HashSet::new);
    }

    void deleteProductOwnersIds(OwnerId ownerId, String itemId) {
        var ops = redisTemplate.boundSetOps(buildProductToOwnerKey(itemId));
        ops.remove(ownerId);

        log.info("Owner {} removed from {} - {}", ownerId, PRODUCT_TO_OWNERS_PREFIX, itemId);
    }

    private String buildCartKey(OwnerId ownerId) {
        return CARTS_PREFIX + ownerId;
    }

    private String buildProductToOwnerKey(String productId) {
        return PRODUCT_TO_OWNERS_PREFIX + productId;
    }
}
