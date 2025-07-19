package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Product(String id, String name, String description, BigDecimal price) {

    public Product updateName(String name) {
        if (name == null) {
            return this;
        }

        return new Product(id, name, description, price);
    }

    public Product updateDescription(String description) {
        if (description == null) {
            return this;
        }

        return new Product(id, name, description, price);
    }

    public Product updatePrice(BigDecimal price) {
        if (price == null) {
            return this;
        }

        return new Product(id, name, description, price);
    }
}
