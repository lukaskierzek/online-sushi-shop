package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Repository
class CartRepository {

    private static final String CARTS_PREFIX = "carts::";
    private static final String PRODUCT_TO_USERS_PREFIX = "product-to-users::";

    private final RedisTemplate<Object, Object> redisTemplate;
    private final Duration ttl;

    CartRepository(RedisTemplate<Object, Object> redisTemplate, @Value("${spring.data.redis.caches.carts.ttl:PT24H}") Duration ttl) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    public Optional<Cart> getCart(String userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(buildCartKey(userId)))
            .map(Cart.class::cast);
    }

    public void saveCart(Cart cart) {
        redisTemplate.opsForValue().set(buildCartKey(cart.getUserId()), cart, ttl);
    }

    public void saveProductUsersIds(String userId, Set<CartItem> cartItems) {
        cartItems.forEach(cartItem -> {
            var ops = redisTemplate.boundSetOps(buildProductToUserKey(cartItem.getProductId()));
            ops.add(userId);
            ops.expire(ttl);
        });
    }

    public Set<String> getProductUsersIds(String productId) {
        return Optional.ofNullable(redisTemplate.opsForSet().members(buildProductToUserKey(productId)))
            .map(objects -> objects.stream()
                .map(String.class::cast)
                .collect(toUnmodifiableSet()))
            .orElseGet(HashSet::new);
    }

    private String buildCartKey(String userId) {
        return CARTS_PREFIX.concat(userId);
    }

    private String buildProductToUserKey(String productId) {
        return PRODUCT_TO_USERS_PREFIX.concat(productId);
    }
}
