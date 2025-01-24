package com.sushiShop.onlineSushiShop.model.dto;

public class mainCategoryDTO {
    private String mainCategoryName;
    private Integer mainCategoryIsHidden;

    public String getMainCategoryName() {
        return mainCategoryName;
    }

    public void setMainCategoryName(String mainCategoryName) {
        this.mainCategoryName = mainCategoryName;
    }

    public Integer getMainCategoryIsHidden() {
        return mainCategoryIsHidden;
    }

    public void setMainCategoryIsHidden(Integer mainCategoryIsHidden) {
        this.mainCategoryIsHidden = mainCategoryIsHidden;
    }
}
