package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    //region Item
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getNonHiddenItems() {
        return itemRepository.findNonHiddenItems();
    }
    //endregion
}
