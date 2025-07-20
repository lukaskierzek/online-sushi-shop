package com.sushiShop.onlineSushiShop.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginPostDTO(
    @NotBlank(message = "Username field cannot be blank!")
    String username,

    @NotBlank(message = "Password field cannot be blank!")
    String password
) {
}
