package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.mapper.MainCategoryMapper;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryDTO;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryPostDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainCategoryDTOService {
    private final MainCategoryService mainCategoryService;
    private final MainCategoryMapper mainCategoryMapper;

    @Autowired
    public MainCategoryDTOService(MainCategoryService mainCategoryService, MainCategoryMapper mainCategoryMapper) {
        this.mainCategoryService = mainCategoryService;
        this.mainCategoryMapper = mainCategoryMapper;
    }

    public List<MainCategoryDTO> getAllMainCategoriesDTO() {
        List<MainCategory> mainCategoryList = mainCategoryService.getAllMainCategories();
        return mainCategoryMapper.mainCategoryListToMainCategoryDTOList(mainCategoryList);
    }

    public MainCategory postNewMainCategoryFromMainCategoryDTO(MainCategoryPostDTO mainCategoryPostDTO) {
        if (mainCategoryPostDTO == null) return null;

        var additionalInformation = new AdditionalInformation();
        additionalInformation.setIsHidden(IsHidden.YES);

        var mainCategory = new MainCategory();
        mainCategory.setMainCategoryName(mainCategoryPostDTO.mainCategoryName());
        mainCategory.setAdditionalInformation(additionalInformation);

        return mainCategoryService.postMainCategory(mainCategory);
    }

    public List<MainCategoryDTO> getNonHiddenMainCategoriesDTO() {
        List<MainCategory> nonHiddenMainCategories = mainCategoryService.getNonHiddenMainCategories();
        return mainCategoryMapper.mainCategoryListToMainCategoryDTOList(nonHiddenMainCategories);
    }
}
