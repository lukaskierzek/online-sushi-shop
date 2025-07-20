package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;
import static pl.lukaskierzek.sushi.shop.service.catalog.service.category.CategoryValidator.*;

@Getter
@AllArgsConstructor(access = PRIVATE)
class Category {
    private final String id;
    private String name;
    private String description;
    private Set<Category> subCategories;

    static Category newCategory(String name, String description) {
        return new Category(
            randomUUID().toString(),
            validateName(name),
            validateDescription(description),
            new HashSet<>()
        );
    }

    void rename(String name) {
        this.name = validateName(name);
    }

    void rewordDescription(String description) {
        this.description = validateDescription(description);
    }

    void addSubCategory(Category category) {
        subCategories.add(validateSubCategory(id, category));
    }

    CategorySnapshot toSnapshot() {
        return new CategorySnapshot(id, name, description);
    }

    record CategorySnapshot(String categoryId, String name, String description) {
    }
}
