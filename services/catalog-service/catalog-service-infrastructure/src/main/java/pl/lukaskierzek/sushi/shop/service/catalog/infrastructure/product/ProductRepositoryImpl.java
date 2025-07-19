package pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product;

import lombok.RequiredArgsConstructor;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.Product;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductRepository;

import java.util.Optional;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public String saveProduct(Product product) {
        var entity = toProductEntity(product);
        return jpaRepository.save(entity).getId();
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return jpaRepository.findById(id)
                .map(this::toProduct);
    }

    @Override
    public void deleteProduct(Product product) {
        jpaRepository.delete(toProductEntity(product));
    }

    private ProductEntity toProductEntity(Product product) {
        return ProductEntity.builder()
                .id(product.id())
                .name(product.name())
                .description(product.description())
                .price(product.price())
                .build();
    }

    private Product toProduct(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }
}
