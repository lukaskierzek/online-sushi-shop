package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.ItemRepository;
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
public class ItemDTOControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getAllItemDTO_shouldReturnListOfAllItemsDTOGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Item1"))
                .andExpect(jsonPath("$.[1].itemName").value("Item2"))
                .andExpect(jsonPath("$.[4].itemName").value("Item4"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[4].itemIsHidden").value(1));
    }

    @Test
    void getNonHiddenItemsDTO_shouldReturnListOfNonHiddenItemsDTOGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item/non-hidden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Item1"))
                .andExpect(jsonPath("$.[1].itemName").value("Item2"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(0));
    }

    @Test
    void getItemDTOById_shouldReturnItemDTOById() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.itemIsHidden").value(0))
                .andExpect(jsonPath("$.itemName").value("Item1"));
    }
}
