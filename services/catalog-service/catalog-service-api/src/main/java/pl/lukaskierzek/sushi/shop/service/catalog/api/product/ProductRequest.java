package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record ProductRequest(
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, message = "Name must be at least 3 characters")
    String name,

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, message = "Description must be at least 10 characters")
    String description
) {
}
