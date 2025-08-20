package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.lukaskierzek.sushi.shop.service.catalog.service.product.Product.ProductSnapshot;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
class ProductController {

    private final ProductService productService;

    //TODO: Controller Methods


    @GetMapping("/{id}")
    public ProductSnapshot get(@PathVariable String id) {
        return productService.getProductDetails(id);
    }


    record ProductRequest(String name, String description, BigDecimal price, String categoryId) {
    }
}

@RestControllerAdvice
class ProductExceptionHandler {

    @ExceptionHandler(InvalidProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidProduct(InvalidProductException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidProductException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleProductNotFound(ProductNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
