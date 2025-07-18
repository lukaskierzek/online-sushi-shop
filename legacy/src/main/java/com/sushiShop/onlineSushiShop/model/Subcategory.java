package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subcategories")
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategories_id")
    private Long subcategoryId;

    @Column(name = "subcategories_name", unique = true)
    private String subcategoryName;

    @CreationTimestamp
    @Column(name = "subcategories_created_at", nullable = false, updatable = false)
    private LocalDateTime subcategoryCreatedAt;

    @UpdateTimestamp
    @Column(name = "subcategories_updated_at", nullable = false)
    private LocalDateTime subcategoryUpdatedAt;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "isHidden", column = @Column(name = "subcategories_is_hidden", nullable = false))
    })
    private AdditionalInformation additionalInformation;

    @ManyToMany(mappedBy = "subcategories")
    @JsonBackReference
    private Set<Item> items = new HashSet<>();

    public Subcategory() {
    }

    public Subcategory(Long subcategoryId, String subcategoryName, LocalDateTime subcategoryCreatedAt, LocalDateTime subcategoryUpdatedAt, AdditionalInformation additionalInformation) {
        this.subcategoryId = subcategoryId;
        this.subcategoryName = subcategoryName;
        this.subcategoryCreatedAt = subcategoryCreatedAt;
        this.subcategoryUpdatedAt = subcategoryUpdatedAt;
        this.additionalInformation = additionalInformation;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public LocalDateTime getSubcategoryCreatedAt() {
        return subcategoryCreatedAt;
    }

    public void setSubcategoryCreatedAt(LocalDateTime subcategoryCreatedAt) {
        this.subcategoryCreatedAt = subcategoryCreatedAt;
    }

    public LocalDateTime getSubcategoryUpdatedAt() {
        return subcategoryUpdatedAt;
    }

    public void setSubcategoryUpdatedAt(LocalDateTime subcategoryUpdatedAt) {
        this.subcategoryUpdatedAt = subcategoryUpdatedAt;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Subcategory{" +
            "subcategoryId=" + subcategoryId +
            ", subcategoryName='" + subcategoryName + '\'' +
            ", subcategoryCreatedAt=" + subcategoryCreatedAt +
            ", subcategoryUpdatedAt=" + subcategoryUpdatedAt +
            ", additionalInformation=" + additionalInformation +
            ", items=" + items +
            '}';
    }
}
