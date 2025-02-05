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
    private final CommentService commentService;

    @Autowired
    public ItemDTOService(ItemMapper itemMapper, ItemService itemService, MainCategoryService mainCategoryService, CommentService commentService) {
        this.itemMapper = itemMapper;
        this.itemService = itemService;
        this.mainCategoryService = mainCategoryService;
        this.commentService = commentService;
    }

    public List<ItemDTO> getAllItemsDTO() {
        List<Item> itemList = itemService.getAllItems();
        List<ItemDTO> itemDTOListNonHiddenSubcategory = getItemsDTONonHiddenSubcategories(itemList);
        return itemDTOListNonHiddenSubcategory;
    }

    public List<ItemDTO> getNonHiddenItemsDTO() {
        List<Item> nonHiddenItemList = itemService.getNonHiddenItems();
        List<ItemDTO> nonHiddenItemListNonHiddenSubcategory = getItemsDTONonHiddenSubcategories(nonHiddenItemList);
        return nonHiddenItemListNonHiddenSubcategory;
    }

    public ItemDTO getNonHiddenItemDTOById(Long itemId) {
        Item nonHiddenItem = itemService.getNonHiddenItemById(itemId);
        List<Item> nonHiddenItemToList = List.of(nonHiddenItem);
        ItemDTO nonHiddenItemDTOByIdNonHiddenSubcategory = getItemsDTONonHiddenSubcategories(nonHiddenItemToList).getFirst();
        return nonHiddenItemDTOByIdNonHiddenSubcategory;
    }

    //TODO: Add subcategories
    public Item putItemFromItemPostDTO(Long itemId, ItemPostDTO itemPostDTO) {
        Item itemById = itemService.getItemById(itemId);

        Comment commentByItemId = commentService.getCommentById(itemById.getComment().getCommentId());
        commentByItemId.setCommentText(itemPostDTO.itemComment());

        MainCategory mainCategoryById = mainCategoryService.getMainCategoryById(itemPostDTO.itemMainCategoryId());

        AdditionalInformation additionalInformation = new AdditionalInformation(IsHidden.fromValue(itemPostDTO.itemIsHidden()));

        itemById.setItemName(itemPostDTO.itemName());
        itemById.setItemActualPrice(itemPostDTO.itemActualPrice());
        itemById.setItemOldPrice(itemPostDTO.itemOldPrice());
        itemById.setItemImageUrl(itemPostDTO.itemImageUrl());
        itemById.setAdditionalInformation(additionalInformation);
        itemById.setComment(commentByItemId);
        itemById.setMainCategory(mainCategoryById);

        return itemService.putItem(itemById);
    }

    public ItemDTO getItemDTOById(Long itemId) {
        Item item = itemService.getItemById(itemId);
        List<Item> itemDTOByIdToList = List.of(item);
        ItemDTO ItemDTOByIdNonHiddenSubcategory = getItemsDTONonHiddenSubcategories(itemDTOByIdToList).getFirst();
        return ItemDTOByIdNonHiddenSubcategory;
    }

    //TODO: Add subcategory adding
    public Item postNewItemFromItemPostDTO(ItemPostDTO itemPostDTO) {
        if (itemPostDTO == null) return null;

        AdditionalInformation additionInformation = new AdditionalInformation(
            IsHidden.fromValue(itemPostDTO.itemIsHidden())
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
        List<ItemDTO> nonHiddenItemsByCategoryNonHiddenSubcategories = getItemsDTONonHiddenSubcategories(nonHiddenItemsByCategory);
        return nonHiddenItemsByCategoryNonHiddenSubcategories;
    }

    public List<ItemDTO> getNonHiddenItemsByNewItemsCategory() {
        List<Item> nonHiddenItemsByNewItemsCategory = itemService.getNonHiddenItemsByNewItemsCategory();
        List<ItemDTO> nonHiddenItemsByNewItemsCategoryWithSubcategoriesNonHidden = getItemsDTONonHiddenSubcategories(nonHiddenItemsByNewItemsCategory);

        return nonHiddenItemsByNewItemsCategoryWithSubcategoriesNonHidden;
    }

    private List<ItemDTO> getItemsDTONonHiddenSubcategories(List<Item> items) {
        return items.stream()
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
    }


}
