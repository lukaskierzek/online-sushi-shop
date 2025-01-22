package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.service.ItemDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "api/onlinesushishop/item")
public class ItemDTOController {
    private final ItemDTOService itemDTOService;

    @Autowired
    public ItemDTOController(ItemDTOService itemDTOService) {
        this.itemDTOService = itemDTOService;
    }

    //region Get
    @GetMapping(path = "all")
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

    @GetMapping(path = "/non-hidden/{itemId}")
    public ResponseEntity<ItemDTO> getNonHiddenItemDTOById(@PathVariable("itemId") Long itemId) {
        try {
            ItemDTO itemDTONonHidden = itemDTOService.getNonHiddenItemDTOById(itemId);
            return itemDTONonHidden != null ? ResponseEntity.ok(itemDTONonHidden) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //endregion
}
