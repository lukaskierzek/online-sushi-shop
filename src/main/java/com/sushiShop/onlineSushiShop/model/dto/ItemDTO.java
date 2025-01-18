package com.sushiShop.onlineSushiShop.model.dto;

public class ItemDTO {
    private Long itemId;
    private String itemName;
    private Integer itemActualPrice;
    private Integer itemOldPrice;
    private String itemImageUrl;
    private Integer itemIsHidden;
    private String itemComment;
    private String itemMainCategory;

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

    public Integer getItemIsHidden() {
        return itemIsHidden;
    }

    public void setItemIsHidden(Integer itemIsHidden) {
        this.itemIsHidden = itemIsHidden;
    }

    public String getItemComment() {
        return itemComment;
    }

    public void setItemComment(String itemComment) {
        this.itemComment = itemComment;
    }

    public String getItemMainCategory() {
        return itemMainCategory;
    }

    public void setItemMainCategory(String itemMainCategory) {
        this.itemMainCategory = itemMainCategory;
    }
}
