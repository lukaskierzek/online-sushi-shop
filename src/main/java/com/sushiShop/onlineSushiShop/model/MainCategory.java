package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "main_categories")
public class MainCategory {

    private static final String COLUMN_NAME = "main_categories_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME + "id")
    private Long mainCategoryId;

    @Column(name = COLUMN_NAME + "name", nullable = false)
    private String mainCategoryName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = COLUMN_NAME + "is_hidden")),
            @AttributeOverride(name = "createdAt", column = @Column(name = COLUMN_NAME + "created_at")),
            @AttributeOverride(name = "updateAt", column = @Column(name = COLUMN_NAME + "update_at"))
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
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}
