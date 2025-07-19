package pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product;

import lombok.RequiredArgsConstructor;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.Product;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductRepository;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public void saveProduct(Product product) {
        var entity = new ProductEntity();
        entity.setName(product.name());
        entity.setDescription(product.description());
        jpaRepository.save(entity);
    }
}
