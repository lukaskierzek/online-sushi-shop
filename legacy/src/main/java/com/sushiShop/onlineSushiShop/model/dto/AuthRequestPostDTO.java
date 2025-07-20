package com.sushiShop.onlineSushiShop.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestPostDTO(
    @NotBlank(message = "Username field cannot be blank!")
    String username,

    @NotBlank(message = "Password field cannot be blank!")
    String password,

    @NotBlank(message = "Email field cannot be blank!")
    @Email(message = "Wrong email!")
    String email
) {
}

//TODO: Add @Size
