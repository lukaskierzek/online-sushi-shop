package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.controller.ItemController;
import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


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
        List<Item> mockAllItems = MockItems.getItems();

        when(itemService.getAllItems()).thenReturn(mockAllItems);

        // Act
        ResponseEntity<List<Item>> response = itemController.getAllItems();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(3);

        assertThat(response.getBody().get(0).getItemName()).isEqualTo("Rools1");
        assertThat(response.getBody().get(1).getItemName()).isEqualTo("Rools2");

        assertThat(response.getBody().get(0).getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.NO);
        assertThat(response.getBody().get(1).getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.YES);

        assertThat(response.getBody().get(0).getMainCategory().getMainCategoryName()).isEqualTo("Main Subcategory 1");
        assertThat(response.getBody().get(1).getMainCategory().getMainCategoryName()).isEqualTo("Main Subcategory 2");

        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void getNonHiddenItems_shouldReturnListOfNonHiddenItems() throws Exception {
        List<Item> mockNonHiddenItems = MockItems.getItems().stream()
            .filter(item -> IsHidden.NO.equals(item.getAdditionalInformation().getIsHidden()))
            .collect(Collectors.toList());

        when(itemService.getNonHiddenItems()).thenReturn(mockNonHiddenItems);

        // Act
        ResponseEntity<List<Item>> response = itemController.getNonHiddenItems();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(2);

        assertThat(response.getBody().getFirst().getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.NO);
        assertThat(response.getBody().getFirst().getAdditionalInformation().getIsHidden()).isNotEqualTo(IsHidden.YES);

        assertThat(response.getBody().get(1).getAdditionalInformation().getIsHidden()).isEqualTo(IsHidden.NO);
        assertThat(response.getBody().get(1).getAdditionalInformation().getIsHidden()).isNotEqualTo(IsHidden.YES);

        assertThat(response.getBody().get(0).getMainCategory().getMainCategoryName()).isEqualTo("Main Subcategory 1");
        assertThat(response.getBody().get(1).getMainCategory().getMainCategoryName()).isEqualTo("Main Subcategory 1");

        verify(itemService, times(1)).getNonHiddenItems();
    }
}
