package com.sushiShop.onlineSushiShop.model.dto;

public class MainCategoryDTO {
    private Long mainCategoryId;
    private String mainCategoryName;
    private Integer mainCategoryIsHidden;

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

    public Integer getMainCategoryIsHidden() {
        return mainCategoryIsHidden;
    }

    public void setMainCategoryIsHidden(Integer mainCategoryIsHidden) {
        this.mainCategoryIsHidden = mainCategoryIsHidden;
    }
}
