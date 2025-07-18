package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTests {

    @Test
    void shouldNewCategoryValidCategory() {
        String name = "Books";
        String description = "A category for books";

        Category category = Category.newCategory(name, description);

        assertNotNull(category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenNameIsTooShort() {
        InvalidCategoryException ex = assertThrows(
            InvalidCategoryException.class,
            () -> Category.newCategory("Bo", "Valid description")
        );
        assertEquals("Category name must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooShort() {
        InvalidCategoryException ex = assertThrows(
            InvalidCategoryException.class,
            () -> Category.newCategory("Books", "No")
        );
        assertEquals("Category description must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldRename() {
        Category category = Category.newCategory("OldName", "Valid description");
        category.rename("NewName");

        assertEquals("NewName", category.getName());
    }

    @Test
    void shouldRewordDescription() {
        Category category = Category.newCategory("Books", "OldDesc");
        category.rewordDescription("New description");

        assertEquals("New description", category.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNameToInvalid() {
        Category category = Category.newCategory("Books", "Valid");

        InvalidCategoryException ex = assertThrows(
            InvalidCategoryException.class,
            () -> category.rename("ab")
        );
        assertEquals("Category name must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDescriptionToInvalid() {
        Category category = Category.newCategory("Books", "Valid");

        InvalidCategoryException ex = assertThrows(
            InvalidCategoryException.class,
            () -> category.rewordDescription("x")
        );
        assertEquals("Category description must be at least 3 characters long", ex.getMessage());
    }

    @Test
    void shouldNewCategorySnapshot() {
        Category category = Category.newCategory("Books", "For all kinds of books");
        var snapshot = category.toSnapshot();

        assertEquals(category.getId(), snapshot.categoryId());
        assertEquals(category.getName(), snapshot.name());
        assertEquals(category.getDescription(), snapshot.description());
    }

    @Test
    void shouldAddSubCategory() {
        var parent = Category.newCategory("Main", "Parent category");
        var sub = Category.newCategory("Child", "Subcategory");

        parent.addSubCategory(sub);

        assertThat(parent.getSubCategories()).contains(sub);
    }

    @Test
    void shouldNotAllowSelfAsSubCategory() {
        var category = Category.newCategory("Main", "Parent");

        var exception = catchThrowable(() -> category.addSubCategory(category));

        assertThat(exception)
            .isInstanceOf(InvalidCategoryException.class)
            .hasMessageContaining("Category cannot be a subcategory of itself");
    }

    @Test
    void shouldPreserveSubCategoryState() {
        var parent = Category.newCategory("Main", "Parent");
        var sub1 = Category.newCategory("Child1", "Sub 1");
        var sub2 = Category.newCategory("Child2", "Sub 2");

        parent.addSubCategory(sub1);
        parent.addSubCategory(sub2);

        Set<Category> subCategories = parent.getSubCategories();

        assertThat(subCategories)
            .hasSize(2)
            .extracting(Category::getName)
            .containsExactlyInAnyOrder("Child1", "Child2");
    }
}
