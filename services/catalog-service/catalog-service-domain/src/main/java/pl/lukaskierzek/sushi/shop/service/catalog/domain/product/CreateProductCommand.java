package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

public record CreateProductCommand(
    String name,
    String description
) {
}
