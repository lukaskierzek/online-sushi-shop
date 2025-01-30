package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
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
                .andExpect(jsonPath("$.[0].itemName").value("Maguro Nigiri"))
                .andExpect(jsonPath("$.[1].itemName").value("California Roll"))
                .andExpect(jsonPath("$.[4].itemName").value("Dragon Roll"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[4].itemIsHidden").value(1))
                .andExpect(jsonPath("$.[0].itemMainCategory").value("Nigiri"))
                .andExpect(jsonPath("$.[1].itemMainCategory").value("Nigiri"))
                .andExpect(jsonPath("$.[4].itemMainCategory").value("Temaki"))
                .andExpect(jsonPath("$.[0].itemSubcategories[*].subcategoryName", hasItem("NEW ITEM")))
                .andExpect(jsonPath("$.[1].itemSubcategories").isEmpty())
                .andExpect(jsonPath("$.[4].itemSubcategories").isEmpty())
        ;
    }

    @Test
    void getNonHiddenItemsDTO_shouldReturnListOfNonHiddenItemsDTOGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item/non-hidden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Maguro Nigiri"))
                .andExpect(jsonPath("$.[1].itemName").value("California Roll"))
                .andExpect(jsonPath("$.[0].itemIsHidden").value(0))
                .andExpect(jsonPath("$.[1].itemIsHidden").value(0));
    }

    @Test
    void getItemDTOById_shouldReturnItemDTOById() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.itemIsHidden").value(0))
                .andExpect(jsonPath("$.itemName").value("Maguro Nigiri"));
    }

    @Test
    void getItemDOByCategory_shouldReturnNonHiddenItemsDTOByNewItemCategory() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/item/non-hidden/by-category?category=NEW ITEM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].itemName").value("Maguro Nigiri"))
                .andExpect(jsonPath("$.[0].itemComment").value("A piece of rice with vinegar, topped with a slice of raw tuna. It is one of the most popular sushi, often served with wasabi and soy sauce."))
                .andExpect(jsonPath("$.[0].itemMainCategory").value("Nigiri"))
                .andExpect(jsonPath("$.[0].itemSubcategories[*].subcategoryName", hasItem("NEW ITEM")));
    }
}
