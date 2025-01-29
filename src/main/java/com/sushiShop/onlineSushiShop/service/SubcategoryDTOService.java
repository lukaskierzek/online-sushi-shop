package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.mapper.SubcategoryMapper;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Subcategory;
import com.sushiShop.onlineSushiShop.model.dto.SubcategoryDTO;
import com.sushiShop.onlineSushiShop.model.dto.SubcategoryPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoryDTOService {
    private final SubcategoryService subcategoryService;
    private final SubcategoryMapper subcategoryMapper;

    @Autowired
    public SubcategoryDTOService(SubcategoryService subcategoryService, SubcategoryMapper subcategoryMapper) {
        this.subcategoryService = subcategoryService;
        this.subcategoryMapper = subcategoryMapper;
    }

    public List<SubcategoryDTO> getAllSubcategoriesDTO() {
        List<Subcategory> subcategoriesList = subcategoryService.getAllSubcategories();
        return subcategoryMapper.subcategoryListToSubcategoryDTOList(subcategoriesList);
    }

    public List<SubcategoryDTO> getAllNonHiddenSubcategoriesDTO() {
        List<Subcategory> nonHiddenSubcategories = subcategoryService.getAllNonHiddenSubcategories();
        return subcategoryMapper.subcategoryListToSubcategoryDTOList(nonHiddenSubcategories);
    }

    public Subcategory postSubcategoryFromSubcategoryPostDTO(SubcategoryPostDTO subcategoryPostDTO) {
        if (subcategoryPostDTO == null) return null;

        AdditionalInformation additionalInformation = new AdditionalInformation();
        additionalInformation.setIsHidden(IsHidden.YES);

        Subcategory subcategory = new Subcategory();
        subcategory.setSubcategoryName(subcategoryPostDTO.subcategoryName());
        subcategory.setAdditionalInformation(additionalInformation);

        return subcategoryService.postSubcategory(subcategory);
    }
}
