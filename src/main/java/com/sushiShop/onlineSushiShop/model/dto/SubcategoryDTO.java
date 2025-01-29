package com.sushiShop.onlineSushiShop.model.dto;

public class SubcategoryDTO {
    private String subcategoryName;
    private Integer subcategoryIsHidden;

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
