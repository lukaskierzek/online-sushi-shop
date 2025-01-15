package com.sushiShop.onlineSushiShop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "main_categories")
public class MainCategory {

    private Long mainCategoryId;
    private String mainCategoryName;
    private AdditionalInformation additionalInformation;
}
