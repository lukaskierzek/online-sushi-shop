package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import lombok.Builder;

@Builder
public record Product(String id, String name, String description) {
}
