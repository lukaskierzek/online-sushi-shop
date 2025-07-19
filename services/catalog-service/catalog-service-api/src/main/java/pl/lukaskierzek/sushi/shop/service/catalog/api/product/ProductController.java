package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.DeleteProductCommand;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.GetProductDetailsQuery;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.ProductService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductService service;
    private final ProductControllerMapper mapper;

    @PostMapping
    @ResponseStatus(CREATED)
    ProductIdResponse post(@Valid @RequestBody ProductRequest request) {
        var command = mapper.toCreateProductCommand(request);
        return new ProductIdResponse(service.saveProduct(command));
    }

    @GetMapping("/{id}")
    ProductDetailsResponse get(@PathVariable String id) {
        var query = new GetProductDetailsQuery(id);
        var product = service.getProductById(query);
        return mapper.toProductDetailsResponse(product);
    }

    @PatchMapping("/{id}")
    void patch(@PathVariable String id, @Valid @RequestBody PatchProductRequest request) {
        var command = mapper.toPatchProductCommand(id, request);
        service.patchProduct(command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    void delete(@PathVariable String id) {
        var command = new DeleteProductCommand(id);
        service.deleteProduct(command);
    }
}
