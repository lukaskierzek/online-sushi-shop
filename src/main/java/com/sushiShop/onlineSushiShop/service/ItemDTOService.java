package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemDTOService {
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @Autowired
    public ItemDTOService(ItemMapper itemMapper, ItemService itemService) {
        this.itemMapper = itemMapper;
        this.itemService = itemService;
    }

    public List<ItemDTO> getAllItemsDTO() {
        List<Item> itemList = itemService.getAllItems();
        return itemMapper.itemListToItemDTOList(itemList);
    }

    public List<ItemDTO> getNonHiddenItemsDTO() {
        var nonHiddenItemList = itemService.getNonHiddenItems();
        return itemMapper.itemListToItemDTOList(nonHiddenItemList);
    }
}
