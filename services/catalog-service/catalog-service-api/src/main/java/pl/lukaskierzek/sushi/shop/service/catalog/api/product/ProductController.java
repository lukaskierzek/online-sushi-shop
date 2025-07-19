package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.GetProductDetailsQuery;
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
    ProductIdResponse post(@Valid @RequestBody ProductRequest request) {
        var product = mapper.toCreateProductCommand(request);
        return new ProductIdResponse(service.saveProduct(product));
    }

    @GetMapping("/{id}")
    ProductDetailsResponse get(@PathVariable String id) {
        var query = new GetProductDetailsQuery(id);
        var product = service.getProductById(query);
        return mapper.toProductDetailsResponse(product);
    }
}
