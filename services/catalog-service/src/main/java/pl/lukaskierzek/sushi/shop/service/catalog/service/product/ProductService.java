package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.CategoryService;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.DatabaseOperation;
import pl.lukaskierzek.sushi.shop.service.catalog.service.product.DomainEvent.ProductPriceUpdated;
import pl.lukaskierzek.sushi.shop.service.catalog.service.product.ProductController.ProductRequest;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    public String createProduct(ProductRequest request) {
        var category = categoryService.getProductCategory(request.categoryId());
        validateProductName(request.name());
        var product = Product.create(request.name(), request.description(), new Money(Currency.PLN, request.price()), category);
        productRepository.saveProduct(product, DatabaseOperation.CREATE);
        return product.getId();
    }

    @EventListener
    public void onProductPriceUpdated(ProductPriceUpdated event) {
        kafkaTemplate.send("pl.lukaskierzek.catalog.product.price-updated", event.toString());
    }

    void validateProductName(String productName) {
        var maybeExistingProductName = productRepository.getProductByName(productName);
        if (maybeExistingProductName.isPresent()) {
            throw new InvalidProductException("Product with provided name already exists");
        }
    }

    public Product.ProductSnapshot getProductDetails(String productId) {
        return getProductById(productId)
            .toSnapshot();
    }

    private Product getProductById(String productId) {
        return productRepository.getProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }


    @Transactional
    public void patchProduct(String id, ProductRequest request) {
        var changed = new AtomicBoolean(false);
        var product = getProductById(id);


        ofNullable(request.name()).ifPresent(name -> {
            if (!name.equals(product.getName())) {
                validateProductName(name);
                product.updateName(name);
                changed.set(true);
            }
        });

        ofNullable(request.price()).ifPresent(price -> {
            if (!price.equals(product.getPrice().amount())) {
                product.updatePrice(new Money(Currency.PLN, price));
                changed.set(true);
            }
        });

        ofNullable(request.description()).ifPresent(description -> {
            if (!description.equals(product.getDescription())) {
                product.updateDescription(description);
                changed.set(true);
            }
        });

        ofNullable(request.categoryId()).ifPresent(categoryId -> {
            var productCategory = categoryService.getProductCategory(categoryId);
            if (!productCategory.equals(product.getCategory())) {
                product.updateCategory(productCategory);
                changed.set(true);
            }
        });

        if (changed.get()) {
            productRepository.saveProduct(product, DatabaseOperation.UPDATE);
        }
    }
}
