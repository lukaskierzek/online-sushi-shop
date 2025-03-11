package com.sushiShop.onlineSushiShop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryDTO;
import com.sushiShop.onlineSushiShop.repository.MainCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MainCategoryDTOControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainCategoryRepository mainCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void addMainCategoryDRO_shouldReturnNewMainCategoryDTO() throws Exception {
        MainCategoryDTO newMainCategoryDTO = new MainCategoryDTO();
        newMainCategoryDTO.setMainCategoryName("NewMainCategoryTEST");
        newMainCategoryDTO.setMainCategoryIsHidden(0);

        mockMvc.perform(post("/api/v1/onlinesushishop/main-category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMainCategoryDTO)))
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.LOCATION, containsString("/api/v1/onlinesushishop/raw/main-category/")))
            .andExpect(jsonPath("$.mainCategoryName").value("NewMainCategoryTEST"))
            .andExpect(jsonPath("$.additionalInformation.isHidden").value(1))
        ;
    }
}
