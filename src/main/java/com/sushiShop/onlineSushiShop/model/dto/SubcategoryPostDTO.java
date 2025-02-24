package com.sushiShop.onlineSushiShop.model.dto;

public record SubcategoryPostDTO(
    Long subcategoryId,
    String subcategoryName,
    Integer subcategoryIsHidden
) {
}
