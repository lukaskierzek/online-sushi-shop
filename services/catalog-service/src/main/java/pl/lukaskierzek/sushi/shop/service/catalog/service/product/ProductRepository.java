package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ProductRepository {

    private final ApplicationEventPublisher eventPublisher;

    void saveProduct(Product product) {
        final var events = product.getEvents();
        product.clearEvents();

        //TODO: save

        events.forEach(eventPublisher::publishEvent);
    }
}
