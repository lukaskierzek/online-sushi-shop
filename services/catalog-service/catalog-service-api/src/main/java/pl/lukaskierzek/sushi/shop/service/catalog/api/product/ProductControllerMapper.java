package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import org.mapstruct.Mapper;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.CreateProductCommand;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.PatchProductCommand;
import pl.lukaskierzek.sushi.shop.service.catalog.domain.product.Product;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
interface ProductControllerMapper {

    CreateProductCommand toCreateProductCommand(ProductRequest request);

    ProductDetailsResponse toProductDetailsResponse(Product product);

    PatchProductCommand toPatchProductCommand(String id, PatchProductRequest request);
}
