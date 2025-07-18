package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.controller.AuthController;
import com.sushiShop.onlineSushiShop.enums.Role;
import com.sushiShop.onlineSushiShop.model.dto.AuthLoginPostDTO;
import com.sushiShop.onlineSushiShop.model.dto.AuthRequestPostDTO;
import com.sushiShop.onlineSushiShop.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldReturnSuccessWhenRegistrationIsValid() {
        AuthRequestPostDTO authRequestPostDTO = new AuthRequestPostDTO(
            "user",
            "pass",
            "user@example.com"
        );

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<String> response = authController.register(authRequestPostDTO, bindingResult);

        verify(authService).register(authRequestPostDTO.username(), authRequestPostDTO.password(), authRequestPostDTO.email(), Role.USER);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User user registered successfully", response.getBody());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {
        AuthRequestPostDTO authRequestPostDTO = new AuthRequestPostDTO(
            "user",
            "pass",
            "user@example.com"
        );

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("email", "Invalid email")));

        ResponseEntity<String> response = authController.register(authRequestPostDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Validation error"));

    }

    @Test
    void shouldReturnSuccessWhenLoginIsValid() {
        AuthLoginPostDTO authLoginPostDTO = new AuthLoginPostDTO(
            "admin",
            "admin1234"
        );
        String token = "mockToken";


        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(authLoginPostDTO.username(), authLoginPostDTO.password())).thenReturn(token);

        ResponseEntity<?> response = authController.login(authLoginPostDTO, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody());
    }

    @Test
    public void shouldReturnUnauthorizedWhenLoginFails() {
        AuthLoginPostDTO authLoginPostDTO = new AuthLoginPostDTO(
            "admin",
            "admin1234"
        );

        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.login(authLoginPostDTO.username(), authLoginPostDTO.password())).thenThrow(new RuntimeException("Login failed"));

        ResponseEntity<?> response = authController.login(authLoginPostDTO, bindingResult);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Wrong username or password!", response.getBody());
    }

}
