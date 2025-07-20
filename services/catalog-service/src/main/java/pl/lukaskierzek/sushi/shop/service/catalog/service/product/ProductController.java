package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
class ProductController {

    private final ProductService productService;

    //TODO: Controller Methods

    record ProductRequest(String name, String description, BigDecimal price, String categoryId) {
    }
}
