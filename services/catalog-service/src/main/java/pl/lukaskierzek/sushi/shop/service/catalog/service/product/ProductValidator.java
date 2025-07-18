package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import lombok.experimental.UtilityClass;

import static java.math.BigDecimal.ZERO;

@UtilityClass
class ProductValidator {

    static String validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new InvalidProductException("Product name must be at least 3 characters long");
        }

        return name;
    }

    static String validateDescription(String description) {
        if (description == null || description.trim().length() < 3) {
            throw new InvalidProductException("Product description must be at least 3 characters long");
        }
        return description;
    }

    static Money validatePrice(Money price) {
        if (price == null) {
            throw new InvalidProductException("Product price must not be null");
        }
        if (price.currency() == null) {
            throw new InvalidProductException("Price currency must not be null");
        }
        if (price.amount() == null) {
            throw new InvalidProductException("Price amount must not be null");
        }
        if (price.amount().compareTo(ZERO) <= 0) {
            throw new InvalidProductException("Price must be greater than 0");
        }

        var precision = price.amount().precision();
        var scale = price.amount().scale();
        var integerPart = precision - scale;

        if (integerPart > 12 || scale > 3) {
            throw new InvalidProductException("Price must have up to 12 digits before and 3 after decimal point");
        }

        return price;
    }
}
