package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

record PatchProductRequest(
    @Size(min = 3, message = "Name must be at least 3 characters")
    String name,

    @Size(min = 10, message = "Description must be at least 10 characters")
    String description,

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Price must have up to 12 digits before and 2 digits after the decimal point")
    BigDecimal price
) {
}
