package pl.lukaskierzek.sushi.shop.service.catalog.service.kernel;

import java.util.Set;

public record ProductCategory(
    String id,
    String name,
    String description,
    Set<ProductCategory> subCategories
) {
}
