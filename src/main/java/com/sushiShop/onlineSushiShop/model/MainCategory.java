package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "main_categories")
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "main_category_seq")
    @SequenceGenerator(name = "main_category_seq", sequenceName = "main_category_sequence", allocationSize = 1)
    @Column(name = "main_categories_id")
    private Long mainCategoryId;

    @Column(name = "main_categories_name", nullable = false, unique = true)
    private String mainCategoryName;

    @CreationTimestamp
    @Column(name =  "main_categories_created_at", nullable = false, updatable = false)
    private LocalDateTime mainCategoryCreatedAt;

    @UpdateTimestamp
    @Column(name = "main_categories_updated_at", nullable = false)
    private LocalDateTime mainCategoryUpdatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "main_categories_is_hidden", nullable = false))
    })
    private AdditionalInformation additionalInformation;

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.PERSIST)
    @JsonBackReference
    private List<Item> items;

    public MainCategory() {
    }

    public MainCategory(Long mainCategoryId, String mainCategoryName, AdditionalInformation additionalInformation) {
        this.mainCategoryId = mainCategoryId;
        this.mainCategoryName = mainCategoryName;
        this.additionalInformation = additionalInformation;
    }

    public Long getMainCategoryId() {
        return mainCategoryId;
    }

    public void setMainCategoryId(Long mainCategoryId) {
        this.mainCategoryId = mainCategoryId;
    }

    public String getMainCategoryName() {
        return mainCategoryName;
    }

    public void setMainCategoryName(String mainCategoryName) {
        this.mainCategoryName = mainCategoryName;
    }

    public LocalDateTime getMainCategoryCreatedAt() {
        return mainCategoryCreatedAt;
    }

    public void setMainCategoryCreatedAt(LocalDateTime mainCategoryCreatedAt) {
        this.mainCategoryCreatedAt = mainCategoryCreatedAt;
    }

    public LocalDateTime getMainCategoryUpdatedAt() {
        return mainCategoryUpdatedAt;
    }

    public void setMainCategoryUpdatedAt(LocalDateTime mainCategoryUpdatedAt) {
        this.mainCategoryUpdatedAt = mainCategoryUpdatedAt;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "MainCategory{" +
                "mainCategoryId=" + mainCategoryId +
                ", mainCategoryName='" + mainCategoryName + '\'' +
                ", mainCategoryCreatedAt=" + mainCategoryCreatedAt +
                ", mainCategoryUpdatedAt=" + mainCategoryUpdatedAt +
                ", additionalInformation=" + additionalInformation +
                ", items=" + items +
                '}';
    }
}
