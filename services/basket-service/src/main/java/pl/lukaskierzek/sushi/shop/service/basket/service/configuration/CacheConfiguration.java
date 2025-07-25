package pl.lukaskierzek.sushi.shop.service.basket.service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
class CacheConfiguration {

    @Bean
    RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
