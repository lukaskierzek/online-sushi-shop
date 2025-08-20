package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.DatabaseOperation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
class ProductRepository {

    private final ProductEntityRepository repository;
    private final ProductHistoryEntityRepository historyRepository;
    private final ApplicationEventPublisher eventPublisher;

    void saveProduct(Product product, DatabaseOperation operation) {
        final var events = product.getEvents();
        product.clearEvents();

        //TODO: save

        repository.save(new ProductEntity(product));
        historyRepository.save(new ProductHistoryEntity(product.toSnapshot(), operation));
        events.forEach(eventPublisher::publishEvent);
    }

    Optional<Product> getProductByName(String name) {
        return repository.findByName(name)
            .map(ProductEntity::getProduct);
    }

    Optional<Product> getProductById(String id) {
        return repository.findById(id)
            .map(ProductEntity::getProduct);
    }

    Set<Product> getProductsByCategoryId(String categoryId) {
        return repository.findByCategoryId(categoryId)
            .stream()
            .map(ProductEntity::getProduct)
            .collect(Collectors.toSet());
    }

}

interface ProductEntityRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByName(String name);
    Set<ProductEntity> findByCategoryId(String id);
}

interface ProductHistoryEntityRepository extends JpaRepository<ProductHistoryEntity, String> {
}

@NoArgsConstructor
@Getter
@Entity
@Table(name = "products")
class ProductEntity {

    @Id
    private String id;

    @Column
    private String name;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Product product;

    @Column
    private String categoryId;

    @Column
    private Money price;

    @Column
    private String description;

    @Version
    private long version;

    ProductEntity(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.product = product;
        this.price = product.getPrice();
        this.categoryId = product.getCategory().id();
        this.description = product.getDescription();
    }
}

@NoArgsConstructor
@Getter
@Entity
@Table(name = "products_history")
class ProductHistoryEntity {

    @Id
    private String id;

    @Column
    private String productId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Product.ProductSnapshot snapshot;

    @Column
    @Enumerated(EnumType.STRING)
    private DatabaseOperation operation;

    private Instant time;

    ProductHistoryEntity(Product.ProductSnapshot snapshot, DatabaseOperation operation) {
        this.id = randomUUID().toString();
        this.productId = snapshot.id();
        this.snapshot = snapshot;
        this.time = now();
        this.operation = operation;
    }
}
