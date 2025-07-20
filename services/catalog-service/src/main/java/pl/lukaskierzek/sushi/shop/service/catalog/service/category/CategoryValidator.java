package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import lombok.experimental.UtilityClass;

@UtilityClass
class CategoryValidator {

    static String validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new InvalidCategoryException("Category name must be at least 3 characters long");
        }
        return name;
    }

    static String validateDescription(String description) {
        if (description == null || description.trim().length() < 3) {
            throw new InvalidCategoryException("Category description must be at least 3 characters long");
        }
        return description;
    }

    static Category validateSubCategory(String categoryId, Category subCategory) {
        if (subCategory.getId().equals(categoryId)) {
            throw new InvalidCategoryException("Category cannot be a subcategory of itself");
        }

        return subCategory;
    }
}
