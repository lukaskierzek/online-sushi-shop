package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.controller.ItemController;
import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getAllItems_shouldReturnListOfAllItems() {
        List<Item> mockAllItems = getItems();

        when(itemService.getAllItems()).thenReturn(mockAllItems);

        // Act
        ResponseEntity<List<Item>> response = itemController.getAllItems();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);

        assertThat(response.getBody().get(0).getItemName()).isEqualTo("Rools1");
        assertThat(response.getBody().get(1).getItemName()).isEqualTo("Rools2");

        assertThat(response.getBody().get(0).getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.NO);
        assertThat(response.getBody().get(1).getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.YES);

        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void getNonHiddenItems_shouldReturnListOfNonHiddenItems() throws Exception {
        List<Item> mockNonHiddenItems = getItems().stream()
                .filter(item -> IsHidden.NO.equals(item.getAdditionalInformation().getIsHidden()))
                .collect(Collectors.toList());

        when(itemService.getNonHiddenItems()).thenReturn(mockNonHiddenItems);

        // Act
        ResponseEntity<List<Item>> response = itemController.getNonHiddenItems();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);

        assertThat(response.getBody().getFirst().getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.NO);
        assertThat(response.getBody().getFirst().getAdditionalInformation().getIsHidden()).isNotEqualTo(IsHidden.YES);

        verify(itemService, times(1)).getNonHiddenItems();
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


        List<Item> mockAllItems = List.of(
                new Item(1L, "Rools1", 20, 10, "http", additionalInformationIsHiddenNo),
                new Item(2L, "Rools2", 20, 10, "http", additionalInformationIsHiddenYes)
        );
        return mockAllItems;
    }
}
