package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemDTOControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    private List<Long> itemsId;

    @BeforeEach
    public void setup() {
        List<Item> savedItems = itemRepository.saveAll(MockItems.getItems());
        itemsId = savedItems.stream()
                .map(Item::getItemId)
                .toList();
    }

    @AfterEach
    public void cleanup() {
        if (itemsId != null && !itemsId.isEmpty())
            itemRepository.deleteAllById(itemsId);
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

    @Test
    void getItemDTOById_shouldReturnItemDTOById() throws Exception {
        Long itemId = itemsId.getFirst();

        mockMvc.perform(get("http://localhost:8080/api/onlinesushishop/item/non-hidden/%d".formatted(itemId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(itemId))
                .andExpect(jsonPath("$.itemIsHidden").value(0))
                .andExpect(jsonPath("$.itemName").value("Rools1"));
    }
}
