package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PatchProductCommand(String id, String name, String description, BigDecimal price) {
}
