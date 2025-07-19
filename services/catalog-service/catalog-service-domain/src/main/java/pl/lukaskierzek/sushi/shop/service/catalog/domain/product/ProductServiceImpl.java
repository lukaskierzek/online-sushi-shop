package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public String saveProduct(CreateProductCommand command) {
        //validation (f.e: product with name already exists)
        var product = Product.builder()
                .name(command.name())
                .description(command.description())
                .price(command.price())
                .build();

        return repository.saveProduct(product);
    }

    @Override
    public Product getProductById(GetProductDetailsQuery query) {
        return repository.getProductById(query.id())
                .orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public void patchProduct(PatchProductCommand command) {
        var product = repository.getProductById(command.id())
                .orElseThrow(ProductNotFoundException::new)
                .updateName(command.name())
                .updateDescription(command.description())
                .updatePrice(command.price());

        // validation

        repository.saveProduct(product);
    }

    @Override
    public void deleteProduct(DeleteProductCommand command) {
        repository.deleteProduct(repository.getProductById(command.id())
                .orElseThrow(ProductNotFoundException::new));
    }
}
