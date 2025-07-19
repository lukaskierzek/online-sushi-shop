package pl.lukaskierzek.sushi.shop.service.catalog.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductRepository;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductService;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductServiceImpl;
import pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product.ProductJpaRepository;
import pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product.ProductRepositoryImpl;

@EnableJpaRepositories
@EntityScan
@Configuration
public class InfrastructureConfiguration {

    @Bean
    ProductRepository productRepository(ProductJpaRepository jpaRepository) {
        return new ProductRepositoryImpl(jpaRepository);
    }

    @Bean
    ProductService productService(ProductRepository repository) {
        return new ProductServiceImpl(repository);
    }
}
