package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public void saveProduct(CreateProductCommand command) {
        //validation (f.e: product with name already exists)

        repository.saveProduct(new Product(null, command.name(), command.description()));
    }
}
