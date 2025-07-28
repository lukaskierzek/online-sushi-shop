package pl.lukaskierzek.sushi.shop.service.basket.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
class KafkaConfiguration {

    @Bean
    DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
            (consumerRecord, exception) -> log.error("DLT: Failed message: {}", consumerRecord, exception),
            new FixedBackOff(3000L, 3)
        );
    }

}
