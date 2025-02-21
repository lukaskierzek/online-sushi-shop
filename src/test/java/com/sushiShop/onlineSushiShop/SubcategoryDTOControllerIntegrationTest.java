package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.SubcategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubcategoryDTOControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Test
    void getAllSubcategoriesDTO_shouldReturnListOfNonHiddenSucategoriesDTO() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/subcategory/non-hidden"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].subcategoryName").value("NEW-ITEM"))
            .andExpect(jsonPath("$.[1].subcategoryName").value("VEGE"))
            .andExpect(jsonPath("$.[0].subcategoryIsHidden").value(0))
            .andExpect(jsonPath("$.[1].subcategoryIsHidden").value(0))

        ;
    }
}
