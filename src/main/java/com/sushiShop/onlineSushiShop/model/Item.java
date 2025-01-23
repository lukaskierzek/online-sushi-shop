package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "Items")
@Check(name = "Items_Actual_Price_and_old_price_check", constraints = "Items_Actual_Price > 0 AND Items_Old_Price > 0")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "item_sequence", allocationSize = 1)
    @Column(name = "Items_Id")
    private Long ItemId;

    @Column(name = "Items_Name", nullable = false)
    private String ItemName;

    @Column(name = "Items_Actual_Price", nullable = false)
    private Integer ItemActualPrice;

    @Column(name = "Items_Old_Price", nullable = false)
    private Integer ItemOldPrice;

    @Column(name = "Items_Image_Url", nullable = false, length = 1024)
    private String ItemImageUrl;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "Items_is_hidden")),
            @AttributeOverride(name = "createdAt", column = @Column(name = "Items_created_at")),
            @AttributeOverride(name = "updateAt", column = @Column(name = "Items_update_at"))
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

    public Item() {
    }

    public Item(Long itemId, String itemName, Integer itemActualPrice, Integer itemOldPrice, String itemImageUrl, AdditionalInformation additionalInformation, Comment comment, MainCategory mainCategory) {
        this.ItemId = itemId;
        this.ItemName = itemName;
        this.ItemActualPrice = itemActualPrice;
        this.ItemOldPrice = itemOldPrice;
        this.ItemImageUrl = itemImageUrl;
        this.additionalInformation = additionalInformation;
        this.comment = comment;
        this.mainCategory = mainCategory;
    }

    public Long getItemId() {
        return ItemId;
    }

    public void setItemId(Long itemId) {
        this.ItemId = itemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        this.ItemName = itemName;
    }

    public Integer getItemActualPrice() {
        return ItemActualPrice;
    }

    public void setItemActualPrice(Integer itemActualPrice) {
        this.ItemActualPrice = itemActualPrice;
    }

    public Integer getItemOldPrice() {
        return ItemOldPrice;
    }

    public void setItemOldPrice(Integer itemOldPrice) {
        this.ItemOldPrice = itemOldPrice;
    }

    public String getItemImageUrl() {
        return ItemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.ItemImageUrl = itemImageUrl;
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

    @Override
    public String toString() {
        return "Item{" +
                "ItemId=" + ItemId +
                ", ItemName='" + ItemName + '\'' +
                ", ItemActualPrice=" + ItemActualPrice +
                ", ItemOldPrice=" + ItemOldPrice +
                ", ItemImageUrl='" + ItemImageUrl + '\'' +
                ", additionalInformation=" + additionalInformation +
                ", comment=" + comment +
                ", mainCategory=" + mainCategory +
                '}';
    }
}
