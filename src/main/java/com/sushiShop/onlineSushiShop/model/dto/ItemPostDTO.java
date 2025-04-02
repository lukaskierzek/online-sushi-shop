package com.sushiShop.onlineSushiShop.model.dto;

import jakarta.validation.constraints.Min;

import java.util.List;

public record ItemPostDTO(
    String itemName,
    @Min(1)
    Integer itemActualPrice,
    @Min(1)
    Integer itemOldPrice,
    String itemImageUrl,
    String itemComment,
    Long itemMainCategoryId,
    Integer itemIsHidden,
    List<Long> itemSubcategoriesId
) {

}
