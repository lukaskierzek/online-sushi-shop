package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setup(){
        itemRepository.deleteAll();
        itemRepository.saveAll(getItems());
    }

    @Test
    void getNonHiddenItems_shouldReturnListOfNonHiddenItemsGet() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/onlinesushishop/item/non-hidden"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName").value("Rools1"))
                .andExpect(jsonPath("$[0].additionalInformation.isHidden").value("0"));
    }

    private static List<Item> getItems() {
        AdditionalInformation additionalInformationIsHiddenNo = new AdditionalInformation(
                IsHidden.NO,
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0)
        );

        AdditionalInformation additionalInformationIsHiddenYes = new AdditionalInformation(
                IsHidden.YES,
                LocalDateTime.of(2023, Month.JANUARY, 11, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 11, 0, 0, 0, 0)
        );


        return List.of(
                new Item(null, "Rools1", 20, 10, "http", additionalInformationIsHiddenNo),
                new Item(null, "Rools2", 20, 10, "http", additionalInformationIsHiddenYes)
        );
    }
}
