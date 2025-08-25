package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import org.junit.jupiter.api.Test;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.ProductCategory;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductTests {

    @Test
    void shouldNewProductHasValidCategory() {
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
        assertNotNull(product.getCategory().id());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(category, product.getCategory());
    }

    @Test
    void shouldThrowExceptionWhenProductCategoryNameIsTooShort() {
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> Product.create("valid name", "valid description", new Money(Currency.PLN, new BigDecimal("50.00")),
                new ProductCategory(
                    "valid category-id",
                    "no",
                    "valid description",
                    Set.of()
                ))
        );

        assertEquals("Product category name must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductCategoryDescriptionIsTooShort() {
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> Product.create("valid name", "valid description", new Money(Currency.PLN, new BigDecimal("50.00")),
                new ProductCategory(
                    "valid category-id",
                    "valid name",
                    "no",
                    Set.of()
                ))
        );

        assertEquals("Product category description must be at least 3 characters long", ex.getMessage());
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

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooShort() {
        InvalidProductException ex = assertThrows(
            InvalidProductException.class,
            () -> Product.create(
                "Valid name",
                "no",
                new Money(Currency.PLN, new BigDecimal("50.00")),
                new ProductCategory(
                    "valid-id",
                    "valid product category",
                    "valid product category description",
                    Set.of()
                ))
        );

        assertEquals("Product description must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldUpdateName() {
        Product product = Product.create(
            "valid name",
            "valid description",
            new Money(Currency.PLN, new BigDecimal("50.00")),
            new ProductCategory(
                "valid-id",
                "valid product category",
                "valid product category description",
                Set.of()
            )
        );

        product.updateName("New valid name");

        assertEquals("New valid name", product.getName());
    }

    @Test
    void shouldUpdatedDescription() {
        Product product = Product.create(
            "valid name",
            "valid description",
            new Money(Currency.PLN, new BigDecimal("50.00")),
            new ProductCategory(
                "valid-id",
                "valid product category",
                "valid product category description",
                Set.of()
            )
        );

        product.updateDescription("New valid description");

        assertEquals("New valid description", product.getDescription());
    }

    @Test
    void shouldUpdatedCategory() {
        Product product = Product.create(
            "valid name",
            "valid description",
            new Money(Currency.PLN, new BigDecimal("50.00")),
            new ProductCategory(
                "valid-id",
                "valid product category",
                "valid product category description",
                Set.of()
            )
        );

        var newValidCategory = new ProductCategory(
            "new-valid-id",
            "new valid product category",
            "new valid product category description",
            Set.of()
        );
        product.updateCategory(newValidCategory);

        assertEquals(newValidCategory, product.getCategory());
    }

    @Test
    void shouldNewProductSnapshot() {
        Product product = Product.create(
            "valid name",
            "valid description",
            new Money(Currency.PLN, new BigDecimal("50.00")),
            new ProductCategory(
                "valid category-id",
                "valid category name",
                "valid category description",
                Set.of()
            ));
        var snapshot = product.toSnapshot();

        assertEquals(product.getId(), snapshot.id());
        assertEquals(product.getName(), snapshot.name());
        assertEquals(product.getDescription(), snapshot.description());
        assertEquals(product.getPrice(), snapshot.price());
        assertEquals(product.getCategory(), snapshot.category());
    }
}
