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
import java.util.Optional;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
class ProductRepository {

    private final ProductEntityRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    void saveProduct(Product product) {
        final var events = product.getEvents();
        product.clearEvents();

        //TODO: save

        events.forEach(eventPublisher::publishEvent);
    }

    Optional<Product> getProductByName(String name) {
        return repository.findByName(name)
            .map(ProductEntity::getProduct);
    }
}

interface ProductEntityRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByName(String name);
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

    @Version
    private long version;

    ProductEntity(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.product = product;
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
