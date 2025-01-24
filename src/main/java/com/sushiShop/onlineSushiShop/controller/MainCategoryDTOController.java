package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.dto.MainCategoryDTO;
import com.sushiShop.onlineSushiShop.service.MainCategoryDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/onlinesushishop/main-category")
public class MainCategoryDTOController {
    private final MainCategoryDTOService mainCategoryDTOService;

    //region Get
    @Autowired
    public MainCategoryDTOController(MainCategoryDTOService mainCategoryDTOService) {
        this.mainCategoryDTOService = mainCategoryDTOService;
    }

    @GetMapping()
    public ResponseEntity<List<MainCategoryDTO>> getAllMainCategoriesDTO() {
        try {
            List<MainCategoryDTO> mainCategoriesDTOList = mainCategoryDTOService.getAllMainCategoriesDTO();
            return ResponseEntity.ok(mainCategoriesDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    //endregion
}
