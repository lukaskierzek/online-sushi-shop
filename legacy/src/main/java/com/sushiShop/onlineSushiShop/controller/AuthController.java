package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.enums.Role;
import com.sushiShop.onlineSushiShop.model.dto.AuthLoginPostDTO;
import com.sushiShop.onlineSushiShop.model.dto.AuthRequestPostDTO;
import com.sushiShop.onlineSushiShop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/onlinesushishop/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestPostDTO authRequestPostDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
//            System.out.println(bindingResult.getAllErrors());

            String errorMessage = errorMessageFromValid(bindingResult);

            return ResponseEntity.badRequest().body("Validation error: " + errorMessage);
        }
        authService.register(
            authRequestPostDTO.username(),
            authRequestPostDTO.password(),
            authRequestPostDTO.email(),
            Role.USER
        );
        return ResponseEntity.ok(String.format("User %s registered successfully", authRequestPostDTO.username()));
    }

    //    TODO: change to @RequestBody
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginPostDTO authLoginPostDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = errorMessageFromValid(bindingResult);
            return ResponseEntity.badRequest().body("Validation error: " + message);
        }

        try {
            String token = authService.login(authLoginPostDTO.username(), authLoginPostDTO.password());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username or password!");
        }
    }


    private String errorMessageFromValid(BindingResult bindingResult) {
        String errorMessage = bindingResult.getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

        return errorMessage;
    }

}

//TODO: Check later
