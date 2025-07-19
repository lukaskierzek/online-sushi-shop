package pl.lukaskierzek.sushi.shop.service.catalog.domain.product;

public interface ProductService {

    String saveProduct(CreateProductCommand command);

    Product getProductById(GetProductDetailsQuery query);
}
