package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.enums.Subcategory;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.model.dto.ItemPostDTO;
import com.sushiShop.onlineSushiShop.service.ItemDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/onlinesushishop/item")
public class ItemDTOController {
    private final ItemDTOService itemDTOService;

    @Autowired
    public ItemDTOController(ItemDTOService itemDTOService) {
        this.itemDTOService = itemDTOService;
    }

    //region Get
    @GetMapping()
    public ResponseEntity<List<ItemDTO>> getAllItemDTO() {
        try {
            List<ItemDTO> itemDTOList = itemDTOService.getAllItemsDTO();
            return ResponseEntity.ok(itemDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "non-hidden")
    public ResponseEntity<List<ItemDTO>> getNonHiddenItemsDTO() {
        try {
            List<ItemDTO> nonHiddenItemsList = itemDTOService.getNonHiddenItemsDTO();
            return ResponseEntity.ok(nonHiddenItemsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "non-hidden/{itemId}")
    public ResponseEntity<?> getNonHiddenItemDTOById(@PathVariable("itemId") Long itemId) {
        ItemDTO itemDTONonHidden = itemDTOService.getNonHiddenItemDTOById(itemId);
        return ResponseEntity.ok(itemDTONonHidden);
    }

    @GetMapping(path = "{itemId}")
    public ResponseEntity<?> getItemDTOById(@PathVariable("itemId") Long itemId) {
        ItemDTO itemDTO = itemDTOService.getItemDTOById(itemId);
        return ResponseEntity.ok(itemDTO);
    }

    @GetMapping(path = "non-hidden/by-category")
    public ResponseEntity<List<ItemDTO>> getNonHiddenItemsDTOByCategory(@RequestParam(name = "category", required = false) String mainCategoryName) {
        try {
            List<ItemDTO> itemDTOList;

            if (!mainCategoryName.isEmpty()) {
                if (mainCategoryName.equals(Subcategory.NEW_ITEM.getValue()))
                    itemDTOList = itemDTOService.getNonHiddenItemsByNewItemsCategory();
                else
                    itemDTOList = itemDTOService.getNonHiddenItemsDTOByCategory(mainCategoryName);
            } else
                itemDTOList = itemDTOService.getNonHiddenItemsDTO();

            return ResponseEntity.ok(itemDTOList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    //endregion

    //region Post
    @PostMapping()
    public ResponseEntity<?> postItemDTO(@RequestBody ItemPostDTO itemPostDTO, UriComponentsBuilder uriComponentsBuilder) {
        try {
            Item itemCreated = itemDTOService.postNewItemFromItemPostDTO(itemPostDTO);
            return ResponseEntity.created(
                uriComponentsBuilder.path("api/v1/onlinesushishop/item/{itemId}")
                    .buildAndExpand(itemCreated.getItemId())
                    .toUri()
            ).body(itemCreated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Error creating item " + itemPostDTO.itemName(),
                "message", e.getMessage()
            ));
        }
    }
    //endregion

    //region Put
    @PutMapping(path = "/{itemId}")
    public ResponseEntity<?> putItemDTO(@PathVariable Long itemId, @RequestBody ItemPostDTO itemPostDTO) {
        Item itemUpdate = itemDTOService.putItemFromItemPostDTO(itemId, itemPostDTO);
        return ResponseEntity.ok(itemUpdate);
    }
    //endregion
}
