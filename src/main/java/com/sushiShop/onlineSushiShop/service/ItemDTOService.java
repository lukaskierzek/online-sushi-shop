package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.mapper.ItemMapper;
import com.sushiShop.onlineSushiShop.model.*;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import com.sushiShop.onlineSushiShop.model.dto.ItemPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    //TODO: Add subcategory adding
    public Item postNewItemFromItemPostDTO(ItemPostDTO itemPostDTO) {
        if (itemPostDTO == null) return null;

        AdditionalInformation additionInformation = new AdditionalInformation(
                IsHidden.YES
        );

        var comment = new Comment(
                null,
                itemPostDTO.itemComment(),
                null,
                null,
                null,
                additionInformation
        );

        MainCategory mainCategory = mainCategoryService.getMainCategoryById(itemPostDTO.itemMainCategoryId());

        Item item = new Item(
                null,
                itemPostDTO.itemName(),
                itemPostDTO.itemActualPrice(),
                itemPostDTO.itemOldPrice(),
                itemPostDTO.itemImageUrl(),
                null,
                null,
                additionInformation,
                comment,
                mainCategory
        );

        return itemService.postItem(item);
    }

    public List<ItemDTO> getNonHiddenItemsDTOByCategory(String mainCategoryName) {
        List<Item> nonHiddenItemsByCategory = itemService.getNonHiddenItemsByCategory(mainCategoryName);
        return itemMapper.itemListToItemDTOList(nonHiddenItemsByCategory);
    }

    public List<ItemDTO> getNonHiddenItemsByNewItemsCategory() {
        List<Item> nonHiddenItemsByNewItemsCategory = itemService.getNonHiddenItemsByNewItemsCategory();
        List<ItemDTO> nonHiddenItemsByNewItemsCategoryWithSubcategoriesNonHidden  = nonHiddenItemsByNewItemsCategory.stream()
                .map(item -> {
                    item.setSubcategories(
                            item.getSubcategories().stream()
                                    .filter(subcategory -> subcategory.getAdditionalInformation().getIsHidden().getValue() == 0)
                                    .sorted(Comparator.comparing(Subcategory::getSubcategoryName))
                                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    );
                    return itemMapper.itemToItemDTO(item);
                })
                .toList();

        return nonHiddenItemsByNewItemsCategoryWithSubcategoriesNonHidden;
    }
}
