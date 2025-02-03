package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Items")
@Check(name = "Items_Actual_Price_and_old_price_check", constraints = "Items_Actual_Price > 0 AND Items_Old_Price > 0")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_sequence", allocationSize = 1)
    @Column(name = "Items_Id")
    private Long itemId;

    @Column(name = "Items_Name", nullable = false)
    private String itemName;

    @Column(name = "Items_Actual_Price", nullable = false)
    private Integer itemActualPrice;

    @Column(name = "Items_Old_Price", nullable = false)
    private Integer itemOldPrice;

    @Column(name = "Items_Image_Url", nullable = false, columnDefinition = "TEXT")
    private String itemImageUrl;

    @CreationTimestamp
    @Column(name = "items_created_at", nullable = false, updatable = false)
    private LocalDateTime itemCreatedAt;

    @UpdateTimestamp
    @Column(name = "items_updated_at", nullable = false)
    private LocalDateTime itemUpdatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "Items_is_hidden", nullable = false))
    })
    private AdditionalInformation additionalInformation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "Items_comments_Id", referencedColumnName = "comments_id")
    @JsonManagedReference
    private Comment comment;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "items_main_categories_id", nullable = false)
    @JsonManagedReference
    private MainCategory mainCategory;

    @ManyToMany
    @JoinTable(
            name = "item_subcategory",
            joinColumns = @JoinColumn(name = "Items_Id"),
            inverseJoinColumns = @JoinColumn(name = "subcategories_id")
    )
    @JsonManagedReference
    private Set<Subcategory> subcategories = new HashSet<>();

    public Item() {
    }

    public Item(Long itemId, String itemName, Integer itemActualPrice, Integer itemOldPrice, String itemImageUrl, LocalDateTime itemCreatedAt, LocalDateTime itemUpdatedAt, AdditionalInformation additionalInformation, Comment comment, MainCategory mainCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemActualPrice = itemActualPrice;
        this.itemOldPrice = itemOldPrice;
        this.itemImageUrl = itemImageUrl;
        this.itemCreatedAt = itemCreatedAt;
        this.itemUpdatedAt = itemUpdatedAt;
        this.additionalInformation = additionalInformation;
        this.comment = comment;
        this.mainCategory = mainCategory;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemActualPrice() {
        return itemActualPrice;
    }

    public void setItemActualPrice(Integer itemActualPrice) {
        this.itemActualPrice = itemActualPrice;
    }

    public Integer getItemOldPrice() {
        return itemOldPrice;
    }

    public void setItemOldPrice(Integer itemOldPrice) {
        this.itemOldPrice = itemOldPrice;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public LocalDateTime getItemCreatedAt() {
        return itemCreatedAt;
    }

    public void setItemCreatedAt(LocalDateTime itemCreatedAt) {
        this.itemCreatedAt = itemCreatedAt;
    }

    public LocalDateTime getItemUpdatedAt() {
        return itemUpdatedAt;
    }

    public void setItemUpdatedAt(LocalDateTime itemUpdatedAt) {
        this.itemUpdatedAt = itemUpdatedAt;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public MainCategory getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }

    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public String toString() {
        return "Item{" +
                "ItemId=" + itemId +
                ", ItemName='" + itemName + '\'' +
                ", ItemActualPrice=" + itemActualPrice +
                ", ItemOldPrice=" + itemOldPrice +
                ", ItemImageUrl='" + itemImageUrl + '\'' +
                ", itemCreatedAt=" + itemCreatedAt +
                ", itemUpdatedAt=" + itemUpdatedAt +
                ", additionalInformation=" + additionalInformation +
                ", comment=" + comment +
                ", mainCategory=" + mainCategory +
                '}';
    }
}
