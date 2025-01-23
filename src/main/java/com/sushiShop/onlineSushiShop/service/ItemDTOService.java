package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.mapper.ItemMapper;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Comment;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.model.dto.ItemPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemDTOService {
    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final MainCategoryService mainCategoryService;

    @Autowired
    public ItemDTOService(ItemMapper itemMapper, ItemService itemService, MainCategoryService mainCategoryService) {
        this.itemMapper = itemMapper;
        this.itemService = itemService;
        this.mainCategoryService = mainCategoryService;
    }

    public List<ItemDTO> getAllItemsDTO() {
        List<Item> itemList = itemService.getAllItems();
        return itemMapper.itemListToItemDTOList(itemList);
    }

    public List<ItemDTO> getNonHiddenItemsDTO() {
        List<Item> nonHiddenItemList = itemService.getNonHiddenItems();
        return itemMapper.itemListToItemDTOList(nonHiddenItemList);
    }

    public ItemDTO getNonHiddenItemDTOById(Long itemId) {
        Item nonHiddenItem = itemService.getNonHiddenItemById(itemId);
        return itemMapper.itemToItemDTO(nonHiddenItem);
    }

    public ItemDTO getItemDTOById(Long itemId) {
        Item item = itemService.getItemById(itemId);
        return itemMapper.itemToItemDTO(item);
    }

    public Item postNewItem(ItemPostDTO itemPostDTO) {
        if (itemPostDTO == null) return null;

        AdditionalInformation additionInformation = new AdditionalInformation(
                IsHidden.YES,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        var comment = new Comment(
                null,
                itemPostDTO.getItemComment(),
                null,
                additionInformation
        );

        MainCategory mainCategory = mainCategoryService.getMainCategoryById(itemPostDTO.getItemMainCategoryId());

        Item item = new Item(
                null,
                itemPostDTO.getItemName(),
                itemPostDTO.getItemActualPrice(),
                itemPostDTO.getItemOldPrice(),
                itemPostDTO.getItemImageUrl(),
                additionInformation,
                comment,
                mainCategory
        );

        return itemService.postItem(item);
    }
}
