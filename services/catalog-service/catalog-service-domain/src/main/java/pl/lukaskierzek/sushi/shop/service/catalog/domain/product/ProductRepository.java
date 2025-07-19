package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import java.util.Optional;

public interface ProductRepository {

    String saveProduct(Product product);

    Optional<Product> getProductById(String id);

    void deleteProduct(Product product);
}
