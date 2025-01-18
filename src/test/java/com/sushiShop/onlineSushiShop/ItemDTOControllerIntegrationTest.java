package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void setup() {
        itemRepository.deleteAll();
        itemRepository.saveAll(MockItems.getItems());
    }

    @Test
    void getAllItemDTO_shouldReturnListOfAllItemsDTOGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/onlinesushishop/item/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Rools1"))
                .andExpect(jsonPath("$.[1].itemName").value("Rools2"))
                .andExpect(jsonPath("$.[2].itemName").value("Rools3"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(1))
                .andExpect(jsonPath("$.[2].itemIsHidden").value(0));
    }

    @Test
    void getNonHiddenItemsDTO_shouldReturnListOfNonHiddenItemsDTOGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/onlinesushishop/item/non-hidden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Rools1"))
                .andExpect(jsonPath("$.[1].itemName").value("Rools3"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(0));
    }
}
