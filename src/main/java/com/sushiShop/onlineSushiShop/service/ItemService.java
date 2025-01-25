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

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getNonHiddenItems() {
        return itemRepository.findNonHiddenItems();
    }

    public Item getNonHiddenItemById(Long itemId) {
        return itemRepository.findNonHiddenItem(itemId)
                .orElseThrow(() -> new IllegalStateException(String.format("Item by %s was not found", itemId)));
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findItemById(itemId)
                .orElseThrow(() -> new IllegalStateException(String.format("Item by %s was not found", itemId)));
    }

    public Item postItem(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> getNonHiddenItemsByCategory(String mainCategoryName) {
        return itemRepository.findNonHiddenItemsByCategory(mainCategoryName);
    }
}
