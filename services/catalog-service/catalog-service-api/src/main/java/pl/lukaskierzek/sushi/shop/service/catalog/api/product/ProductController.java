package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductService;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductService service;
    private final ProductControllerMapper mapper;

    @PostMapping
    @ResponseStatus(CREATED)
    void post(@Valid @RequestBody ProductRequest request) {
        var product = mapper.toCreateProductCommand(request);
        service.saveProduct(product);
    }
}
