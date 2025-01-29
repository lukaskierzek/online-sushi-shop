package com.sushiShop.onlineSushiShop.mapper;

import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Subcategory;
import com.sushiShop.onlineSushiShop.model.dto.SubcategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {

    @Mappings({
            @Mapping(source = "additionalInformation", target = "subcategoryIsHidden", qualifiedByName = "mapIsHidden"),
    })
    SubcategoryDTO subcategoryToSubcategoryDTO(Subcategory subcategory);

    List<SubcategoryDTO> subcategoryListToSubcategoryDTOList(List<Subcategory> subcategories);

    @Named("mapIsHidden")
    default Integer mapIsHidden(AdditionalInformation additionalInformation) {
        return additionalInformation != null && additionalInformation.getIsHidden() != null ? additionalInformation.getIsHidden().getValue() : null;
    }
}
