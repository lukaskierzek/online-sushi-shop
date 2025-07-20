package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.Category.CategorySnapshot;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.DatabaseOperation;

import java.time.Instant;
import java.util.Optional;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;

@Repository
@RequiredArgsConstructor
class CategoryRepository {

    private final CategoryEntityRepository repository;
    private final CategoryHistoryEntityRepository historyRepository;

    void saveCategory(Category category, DatabaseOperation operation) {
        repository.save(new CategoryEntity(category));
        historyRepository.save(new CategoryHistoryEntity(category.toSnapshot(), operation));
    }

    Optional<Category> getCategoryById(String id) {
        return repository.findById(id)
            .map(CategoryEntity::getCategory);
    }

    Optional<Category> getCategoryByName(String name) {
        return repository.findByName(name)
            .map(CategoryEntity::getCategory);
    }
}

interface CategoryEntityRepository extends JpaRepository<CategoryEntity, String> {

    Optional<CategoryEntity> findByName(String name);
}

interface CategoryHistoryEntityRepository extends JpaRepository<CategoryHistoryEntity, String> {
}

@NoArgsConstructor
@Getter
@Entity
@Table(name = "categories")
class CategoryEntity {

    @Id
    private String id;

    @Column
    private String name;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Category category;

    @Version
    private long version;

    CategoryEntity(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.category = category;
    }
}

@NoArgsConstructor
@Getter
@Entity
@Table(name = "categories_history")
class CategoryHistoryEntity {

    @Id
    private String id;

    @Column
    private String categoryId;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private CategorySnapshot snapshot;

    @Column
    @Enumerated(EnumType.STRING)
    private DatabaseOperation operation;

    private Instant time;

    CategoryHistoryEntity(CategorySnapshot snapshot, DatabaseOperation operation) {
        this.id = randomUUID().toString();
        this.categoryId = snapshot.categoryId();
        this.snapshot = snapshot;
        this.time = now();
        this.operation = operation;
    }
}
