package pl.lukaskierzek.sushi.shop.service.basket.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
class CacheConfiguration {

    @Bean("redisCacheManager")
    CacheManager redisCacheManager(RedisConnectionFactory connectionFactory, @Value("${spring.data.redis.caches.carts.ttl}") Duration ttl) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl);

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .build();
    }
}
