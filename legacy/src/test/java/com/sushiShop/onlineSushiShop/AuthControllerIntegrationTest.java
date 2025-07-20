package com.sushiShop.onlineSushiShop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushiShop.onlineSushiShop.model.dto.AuthRequestPostDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        AuthRequestPostDTO userDTO = new AuthRequestPostDTO(
            "testusername",
            "password",
            "testusername@testusername.com"
        );

        mockMvc.perform(post("/api/v1/onlinesushishop/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isOk())
            .andExpect(content().string(Matchers.containsString("User testusername registered successfully")));

    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        AuthRequestPostDTO userDTO = new AuthRequestPostDTO(
            "testusername",
            "password",
            "isValidEmail"
        );

        mockMvc.perform(post("/api/v1/onlinesushishop/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Validation error")));
    }

    @Test
    public void shouldReturnOkWhenLoginIsSuccessful() throws  Exception {
        String token = "mockToken";
    }
}
