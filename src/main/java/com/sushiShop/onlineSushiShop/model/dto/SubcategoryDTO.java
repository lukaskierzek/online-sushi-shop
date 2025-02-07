package com.sushiShop.onlineSushiShop.model.dto;

public class SubcategoryDTO {
    private Long subcategoryId;
    private String subcategoryName;
    private Integer subcategoryIsHidden;

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryName() {
        return subcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        this.subcategoryName = subcategoryName;
    }

    public Integer getSubcategoryIsHidden() {
        return subcategoryIsHidden;
    }

    public void setSubcategoryIsHidden(Integer subcategoryIsHidden) {
        this.subcategoryIsHidden = subcategoryIsHidden;
    }
}
