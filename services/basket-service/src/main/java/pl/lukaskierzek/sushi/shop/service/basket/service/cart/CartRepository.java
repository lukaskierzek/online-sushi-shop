package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@Repository
@RequiredArgsConstructor
class CartRepository {

    private static final String CARTS_PREFIX = "carts::";
    private static final String PRODUCT_TO_USERS_PREFIX = "product-to-users::";

    private final RedisTemplate<Object, Object> redisTemplate;

    public Optional<Cart> getCart(String userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(CARTS_PREFIX + userId))
                .map(Cart.class::cast);
    }

    public void saveCart(Cart cart) {
        redisTemplate.opsForValue().set(CARTS_PREFIX + cart.getUserId(), cart);
    }

    public void saveProductUsersIds(String userId, Set<CartItem> cartItems) {
        cartItems
                .forEach(cartItem -> redisTemplate.opsForSet().add(PRODUCT_TO_USERS_PREFIX + cartItem.getProductId(), userId));
    }

    public Set<String> getProductUsersIds(String productId) {
        var productIdMembers = redisTemplate.opsForSet().members(PRODUCT_TO_USERS_PREFIX + productId);

        return Optional.ofNullable(productIdMembers).stream()
                .map(Objects::toString)
                .collect(toUnmodifiableSet());
    }
}
