package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

record ProductRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, message = "Name must be at least 3 characters")
        String name,

        @NotBlank(message = "Description cannot be blank")
        @Size(min = 10, message = "Description must be at least 10 characters")
        String description,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @NotNull(message = "Price cannot be null")
        @Digits(integer = 12, fraction = 2, message = "Price must have up to 12 digits before and 2 digits after the decimal point")
        BigDecimal price
) {
}
