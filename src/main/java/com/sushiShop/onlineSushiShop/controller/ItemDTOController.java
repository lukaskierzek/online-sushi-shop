package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.service.ItemDTOService;
import com.sushiShop.onlineSushiShop.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<ItemDTO>> getAllItemDTO(){
        List<ItemDTO> itemDTOList = itemDTOService.getAllItemsDTO();
        return new ResponseEntity<>(itemDTOList, HttpStatus.OK);
    }

    @GetMapping(path = "non-hidden")
    public ResponseEntity<List<ItemDTO>> getNonHiddenItemsDTO() {
        List<ItemDTO> nonHiddenItemsList = itemDTOService.getNonHiddenItemsDTO();
        return new ResponseEntity<>(nonHiddenItemsList, HttpStatus.OK);
    }
    //endregion
}
