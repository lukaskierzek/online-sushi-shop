package com.sushiShop.onlineSushiShop.model.dto;

public class ItemPostDTO {
    private String itemName;
    private Integer itemActualPrice;
    private Integer itemOldPrice;
    private String itemImageUrl;
    private String itemComment;
    private Long itemMainCategoryId;

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

    public String getItemComment() {
        return itemComment;
    }

    public void setItemComment(String itemComment) {
        this.itemComment = itemComment;
    }

    public Long getItemMainCategoryId() {
        return itemMainCategoryId;
    }

    public void setItemMainCategoryId(Long itemMainCategoryId) {
        this.itemMainCategoryId = itemMainCategoryId;
    }
}
