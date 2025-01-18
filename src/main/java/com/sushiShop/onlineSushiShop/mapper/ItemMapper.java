package com.sushiShop.onlineSushiShop.mapper;

import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mappings({
            @Mapping(source = "additionalInformation", target = "itemIsHidden", qualifiedByName = "mapIsHidden"),
            @Mapping(source = "mainCategory.mainCategoryName", target = "itemMainCategory"),
            @Mapping(source = "comment.commentText", target = "itemComment")
    })
    ItemDTO itemToItemDTO(Item item);

    List<ItemDTO> itemListToItemDTOList(List<Item> items);

    @Named("mapIsHidden")
    default Integer mapIsHidden(AdditionalInformation additionalInformation) {
        if (additionalInformation != null && additionalInformation.getIsHidden() != null)
            return additionalInformation.getIsHidden().getValue();
        return null;
    }
}
