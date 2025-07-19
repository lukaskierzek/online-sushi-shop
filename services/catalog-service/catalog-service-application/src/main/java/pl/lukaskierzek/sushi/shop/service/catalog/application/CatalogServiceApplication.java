package pl.lukaskierzek.sushi.shop.service.catalog.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.lukaskierzek.sushi.shop.service.catalog.api.ApiConfiguration;
import pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.InfrastructureConfiguration;

@SpringBootApplication(scanBasePackageClasses = {InfrastructureConfiguration.class, ApiConfiguration.class})
public class CatalogServiceApplication {

    public static void main(String... args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
}
