package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import org.junit.jupiter.api.Test;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.ProductCategory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTests {

    @Test
    void shouldNewProductValidCategory() {
        String name = "Sushi";
        String description = "A product description for Sushi";
        Money price = new Money(
            Currency.PLN,
            new BigDecimal("50.00")
        );
        ProductCategory category = new ProductCategory(
            "category-id",
            "MainCategory",
            "MainCategoryDescription",
            Set.of()
        );

        Product product = Product.create(name, description, price, category);

        assertNotNull(product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
    }

    @Test
    void shouldThrowExceptionWhenNameIsTooShort() {
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> Product.create(
                "Su",
                "Valid description",
                new Money(Currency.PLN, new BigDecimal("50.00")),
                new ProductCategory(
                    "category-id",
                    "MainCategory",
                    "MainCategoryDescription",
                    Set.of()
                ))
            );

        assertEquals("Product name must be at least 3 characters long", ex.getMessage());
    }
}
