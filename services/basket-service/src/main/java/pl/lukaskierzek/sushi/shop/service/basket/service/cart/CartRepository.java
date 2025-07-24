package pl.lukaskierzek.sushi.shop.service.basket.service.cart;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "carts", cacheManager = "redisCacheManager")
class CartRepository {

    @Cacheable
    public Cart getCart(String userId) {
        // ignored
        return null;
    }

    @CachePut(key = "#cart.userId")
    public Cart setCart(Cart cart) {
        return cart;
    }

    @CacheEvict
    public void evictCart(String userId) {
        // ignored
    }
}
