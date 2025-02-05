package com.sushiShop.onlineSushiShop.model.dto;

public record ItemPostDTO(
        String itemName,
        Integer itemActualPrice,
        Integer itemOldPrice,
        String itemImageUrl,
        String itemComment,
        Long itemMainCategoryId,
        Integer itemIsHidden
) {
}