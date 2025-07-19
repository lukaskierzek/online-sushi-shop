package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import java.math.BigDecimal;

public record CreateProductCommand(
    String name,
    String description,
    BigDecimal price
) {
}
