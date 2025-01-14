package com.sushiShop.onlineSushiShop.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Items_Id")
    private Long ItemId;

    @Column(name = "Items_Name", nullable = false)
    private String ItemName;

    @Column(name = "Items_Actual_Price", nullable = false)
    private Integer ItemActualPrice;

    @Column(name = "Items_Old_Price", nullable = false)
    private Integer ItemOldPrice;

    @Column(name = "Items_Image_Url", nullable = false, length = 500)
    private String ItemImageUrl;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "Items_is_hidden")),
            @AttributeOverride(name = "createdAt", column = @Column(name = "Items_created_at")),
            @AttributeOverride(name = "updateAt", column = @Column(name = "Items_update_at"))
    })
    private AdditionalInformation additionalInformation;

    public Item() {
    }

    public Item(Long itemId, String itemName, Integer itemActualPrice, Integer itemOldPrice, String itemImageUrl, AdditionalInformation additionalInformation) {
        this.ItemId = itemId;
        this.ItemName = itemName;
        this.ItemActualPrice = itemActualPrice;
        this.ItemOldPrice = itemOldPrice;
        this.ItemImageUrl = itemImageUrl;
        this.additionalInformation = additionalInformation;
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

    @Override
    public String toString() {
        return "Item{" +
                "ItemId=" + ItemId +
                ", ItemName='" + ItemName + '\'' +
                ", ItemActualPrice=" + ItemActualPrice +
                ", ItemOldPrice=" + ItemOldPrice +
                ", ItemImageUrl='" + ItemImageUrl + '\'' +
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}
