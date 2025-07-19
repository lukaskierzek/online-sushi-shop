package pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String description;
}

