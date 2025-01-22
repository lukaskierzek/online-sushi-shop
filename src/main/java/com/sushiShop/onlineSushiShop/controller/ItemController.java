package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

//	http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping(path = "api/onlinesushishop/raw/item")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    //region Get
    @GetMapping(path = "all")
    public ResponseEntity<List<Item>> getAllItems() {
        try {
            List<Item> allItemsList = itemService.getAllItems();
            return ResponseEntity.ok(allItemsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "non-hidden")
    public ResponseEntity<List<Item>> getNonHiddenItems() {
        try {
            List<Item> nonHiddenItemsList = itemService.getNonHiddenItems();
            return ResponseEntity.ok(nonHiddenItemsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "non-hidden/{itemId}")
    public ResponseEntity<Item> getNonHiddenItemById(@PathVariable("itemId") Long itemId) {
//        Item nonHiddenItem = itemService.getNonHiddenItemById(itemId);
//        return new ResponseEntity<>(nonHiddenItem, HttpStatus.OK);
        try {
            Item nonHiddenItem = itemService.getNonHiddenItemById(itemId);
            return nonHiddenItem != null ? ResponseEntity.ok(nonHiddenItem) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
    //endregion
}
