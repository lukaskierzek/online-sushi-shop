package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.Category.CategorySnapshot;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.CategoryController.CategoryRequest;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.ProductCategory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.DatabaseOperation.CREATE;
import static pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.DatabaseOperation.UPDATE;

public interface CategoryService {
    ProductCategory getProductCategory(String categoryId);
}

@Service
@RequiredArgsConstructor
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public ProductCategory getProductCategory(String categoryId) {
        var category = getCategoryById(categoryId);
        return toProductCategory(category);
    }

    @Transactional
    public String createCategory(CategoryRequest request) {
        var category = Category.newCategory(request.name(), request.description());
        validateCategoryName(category.getName());
        repository.saveCategory(category, CREATE);
        return category.getId();
    }

    public CategorySnapshot getCategoryDetails(String categoryId) {
        return getCategoryById(categoryId)
            .toSnapshot();
    }

    @Transactional
    public void patchCategory(String id, CategoryRequest request) {
        var changed = new AtomicBoolean(false);
        var category = getCategoryById(id);

        ofNullable(request.name()).ifPresent(name -> {
            changed.set(true);

            if (!name.equals(category.getName())) {
                validateCategoryName(name);
            }

            category.rename(name);
        });

        ofNullable(request.description()).ifPresent(description -> {
            changed.set(true);
            category.rewordDescription(description);
        });

        ofNullable(request.subCategories())
            .filter(Predicate.not(CollectionUtils::isEmpty))
            .ifPresent(subCategories -> {
                changed.set(true);

                subCategories.parallelStream()
                    .map(this::getCategoryById)
                    .forEach(category::addSubCategory);
            });

        if (changed.get()) {
            repository.saveCategory(category, UPDATE);
        }
    }

    private Category getCategoryById(String categoryId) {
        return repository.getCategoryById(categoryId)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
    }

    private void validateCategoryName(String categoryName) {
        var maybeExistingCategory = repository.getCategoryByName(categoryName);
        if (maybeExistingCategory.isPresent()) {
            throw new InvalidCategoryException("Category with provided name already exists");
        }
    }

    private ProductCategory toProductCategory(Category category) {
        return new ProductCategory(category.getId(), category.getName(), category.getDescription(), category.getSubCategories().stream()
            .map(this::toProductCategory)
            .collect(toUnmodifiableSet()));
    }
}
