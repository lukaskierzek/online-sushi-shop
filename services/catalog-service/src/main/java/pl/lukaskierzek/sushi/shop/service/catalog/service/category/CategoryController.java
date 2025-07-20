package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.Category.CategorySnapshot;

import java.util.Map;
import java.util.Set;

import static java.net.URI.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
class CategoryController {

    private final CategoryServiceImpl service;

    @PostMapping
    public ResponseEntity<String> post(@RequestBody CategoryRequest request) {
        String id = service.createCategory(request);
        return created(create("/categories/" + id)).body(id);
    }

    @PatchMapping("/{id}")
    public void patch(@PathVariable String id, @RequestBody CategoryRequest request) {
        service.patchCategory(id, request);
    }

    @GetMapping("/{id}")
    public CategorySnapshot get(@PathVariable String id) {
        return service.getCategoryDetails(id);
    }

    public record CategoryRequest(String name, String description, Set<String> subCategories) {
    }
}

@RestControllerAdvice
class CategoryExceptionHandler {

    @ExceptionHandler(InvalidCategoryException.class)
    @ResponseStatus(BAD_REQUEST)
    public Map<String, String> handleInvalidCategory(InvalidCategoryException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public Map<String, String> handleCategoryNotFound(CategoryNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}

