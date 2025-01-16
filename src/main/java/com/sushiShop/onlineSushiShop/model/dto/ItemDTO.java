package com.sushiShop.onlineSushiShop.model.dto;

import com.sushiShop.onlineSushiShop.enums.IsHidden;

public class ItemDTO {
    private Long ItemId;
    private String ItemName;
    private Integer ItemActualPrice;
    private Integer ItemOldPrice;
    private String ItemImageUrl;
    private IsHidden ItemIsHidden;
    private String ItemComment;
    private String ItemMainCategory;
}
