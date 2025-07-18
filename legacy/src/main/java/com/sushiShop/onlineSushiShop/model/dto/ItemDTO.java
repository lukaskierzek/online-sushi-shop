package com.sushiShop.onlineSushiShop.model.dto;

import jakarta.validation.constraints.Min;

import java.util.List;

public class ItemDTO {
    private Long itemId;
    private String itemName;

    //TODO: Add more validation
    @Min(1)
    private Integer itemActualPrice;
    @Min(1)
    private Integer itemOldPrice;
    private String itemImageUrl;
    private Integer itemIsHidden;
    private String itemComment;
    private String itemMainCategory;
    private List<SubcategoryDTO> itemSubcategories;

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

    public List<SubcategoryDTO> getItemSubcategories() {
        return itemSubcategories;
    }

    public void setItemSubcategories(List<SubcategoryDTO> itemSubcategories) {
        this.itemSubcategories = itemSubcategories;
    }
}
