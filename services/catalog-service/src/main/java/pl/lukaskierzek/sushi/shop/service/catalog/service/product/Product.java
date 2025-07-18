package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.ProductCategory;
import pl.lukaskierzek.sushi.shop.service.catalog.service.product.DomainEvent.ProductPriceUpdated;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;
import static pl.lukaskierzek.sushi.shop.service.catalog.service.product.ProductValidator.*;

@AllArgsConstructor(access = PRIVATE)
@Getter
class Product {

    final String id;
    private String name;
    private String description;
    private Money price;
    private ProductCategory category;
    private final List<DomainEvent> events;

    static Product create(String name, String description, Money price, ProductCategory category) {
        return new Product(
                randomUUID().toString(),
                validateName(name),
                validateDescription(description),
                validatePrice(price),
                category,
                new ArrayList<>());
    }

    Product updateName(String name) {
        this.name = validateName(name);
        return this;
    }

    Product updateDescription(String description) {
        this.description = validateDescription(description);
        return this;
    }

    Product updatePrice(Money price) {
        this.price = validatePrice(price);
        events.add(new ProductPriceUpdated(id, price));
        return this;
    }

    Product updateCategory(ProductCategory category) {
        this.category = category;
        return this;
    }

    List<DomainEvent> getEvents() {
        return unmodifiableList(events);
    }

    void clearEvents() {
        events.clear();
    }

    ProductSnapshot toSnapshot() {
        return new ProductSnapshot(id, name, description, price, category);
    }

    record ProductSnapshot(String id, String name, String description, Money price, ProductCategory category) {
    }
}
