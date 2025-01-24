package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.enums.Category;
import com.sushiShop.onlineSushiShop.repository.ItemRepository;
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
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    private List<Long> itemsId;


    @Test
    void getNonHiddenItems_shouldReturnListOfNonHiddenItemsGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/raw/item/non-hidden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Item1"))
                .andExpect(jsonPath("$[1].itemName").value("Item2"))
                .andExpect(jsonPath("$[0].additionalInformation.isHidden").value(0))
                .andExpect(jsonPath("$[1].additionalInformation.isHidden").value(0))
                .andExpect(jsonPath("$[0].mainCategory.mainCategoryName").value(Category.NEW_ITEMS.getValue()))
                .andExpect(jsonPath("$[1].mainCategory.mainCategoryName").value(Category.NEW_ITEMS.getValue()))
                .andExpect(jsonPath("$[0].comment.commentText").value("Comment text 1"))
                .andExpect(jsonPath("$[1].comment.commentText").value("Comment text 2"));
    }

    @Test
    void getAllItems_shouldReturnListOfAllItemsGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/raw/item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].additionalInformation.isHidden").value("0"))
                .andExpect(jsonPath("$.[4].additionalInformation.isHidden").value("1"))
                .andExpect(jsonPath("$.[2].additionalInformation.isHidden").value("0"));
    }

    @Test
    void getItemById_shouldReturnItemById() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/onlinesushishop/raw/item/non-hidden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.additionalInformation.isHidden").value(0))
                .andExpect(jsonPath("$.itemName").value("Item1"));
    }
}
