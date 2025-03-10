package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.MainCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MainCategoryDTOControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainCategoryRepository mainCategoryRepository;

    @Test
    void getMainCategoryDTO_shouldReturnListOfNonHiddenMainCategoryDTO() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/main-category/non-hidden"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].mainCategoryName").value("Nigiri"))
            .andExpect(jsonPath("$.[0].mainCategoryIsHidden").value(0))
            .andExpect(jsonPath("$.[1].mainCategoryName").value("Maki"))
            .andExpect(jsonPath("$.[1].mainCategoryIsHidden").value(0))
            .andExpect(jsonPath("$.[4].mainCategoryName").value("MainCategoryForTests"))
            .andExpect(jsonPath("$.[4].mainCategoryIsHidden").value(0));
    }
}
