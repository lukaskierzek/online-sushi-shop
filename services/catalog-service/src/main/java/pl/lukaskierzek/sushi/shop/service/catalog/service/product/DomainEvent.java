package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import pl.lukaskierzek.sushi.shop.service.catalog.service.kernel.ProductCategory;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

@JsonTypeInfo(use = NAME)
@JsonSubTypes({
        @Type(value = DomainEvent.ProductPriceUpdated.class, name = "ProductPriceUpdated_v1"),
        @Type(value = DomainEvent.ProductNameUpdated.class, name = "ProductNameUpdated_v1"),
        @Type(value = DomainEvent.ProductDescriptionUpdated.class, name = "ProductDescriptionUpdated_v1")
})
interface DomainEvent {

    record ProductPriceUpdated(String id, Money price) implements DomainEvent {

        @NonNull
        @Override
        public String toString() {
            return new ToStringBuilder(this, JSON_STYLE)
                    .append("id", id)
                    .append("price", price)
                    .toString();
        }
    }

    record ProductNameUpdated(String id, String name) implements DomainEvent {

        @NonNull
        @Override
        public String toString() {
            return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("name", name)
                .toString();
        }
    }

    record ProductDescriptionUpdated(String id, String description) implements DomainEvent {

        @NonNull
        @Override
        public String toString() {
            return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("description", description)
                .toString();
        }
    }

    record ProductCategoryUpdated(String id, ProductCategory category) implements DomainEvent {

        @NonNull
        @Override
        public String toString() {
            return new ToStringBuilder(this, JSON_STYLE)
                .append("id", id)
                .append("category", category)
                .toString();
        }
    }
}
