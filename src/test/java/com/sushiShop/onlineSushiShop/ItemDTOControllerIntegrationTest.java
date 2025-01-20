package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        itemRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE item_sequence RESTART WITH 1");
        itemRepository.saveAll(MockItems.getItems());
    }

    @AfterEach
    public void cleanup() {
        itemRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE item_sequence RESTART WITH 1");
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
        mockMvc.perform(get("http://localhost:8080/api/onlinesushishop/item/non-hidden/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.itemIsHidden").value(0))
                .andExpect(jsonPath("$.itemName").value("Rools1"));
    }
}
