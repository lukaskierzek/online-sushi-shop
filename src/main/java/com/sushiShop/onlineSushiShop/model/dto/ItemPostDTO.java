package com.sushiShop.onlineSushiShop.model.dto;

import java.util.List;

public record ItemPostDTO(
    String itemName,
    Integer itemActualPrice,
    Integer itemOldPrice,
    String itemImageUrl,
    String itemComment,
    Long itemMainCategoryId,
    Integer itemIsHidden,
    List<Long> itemSubcategoriesId
) {

}
