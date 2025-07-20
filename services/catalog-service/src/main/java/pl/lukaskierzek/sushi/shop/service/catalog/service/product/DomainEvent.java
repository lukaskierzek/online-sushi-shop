package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

@JsonTypeInfo(use = NAME)
@JsonSubTypes({
        @Type(value = DomainEvent.ProductPriceUpdated.class, name = "ProductPriceUpdated_v1")
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
}
