package com.sushiShop.onlineSushiShop.mapper;

import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper {

    @Mappings({
        @Mapping(source = "additionalInformation", target = "mainCategoryIsHidden", qualifiedByName = "mapIsHidden"),
        @Mapping(source = "mainCategoryId", target = "mainCategoryId")
    })
    MainCategoryDTO mainCategoryToMainCategoryDTO(MainCategory mainCategory);

    List<MainCategoryDTO> mainCategoryListToMainCategoryDTOList(List<MainCategory> mainCategories);

    @Named("mapIsHidden")
    default Integer mapIsHidden(AdditionalInformation additionalInformation) {
        return additionalInformation != null && additionalInformation.getIsHidden() != null ? additionalInformation.getIsHidden().getValue() : null;
    }
}
