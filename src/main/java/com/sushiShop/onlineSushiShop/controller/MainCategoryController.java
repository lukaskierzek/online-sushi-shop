package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.service.MainCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/onlinesushishop/main-category")
public class MainCategoryController {
    private final MainCategoryService mainCategoryService;

    @Autowired
    public MainCategoryController(MainCategoryService mainCategoryService) {
        this.mainCategoryService = mainCategoryService;
    }

    //region Get
    @GetMapping()
    public ResponseEntity<List<MainCategory>> getAllMainCategories() {
        try {
            List<MainCategory> allMainCategoriesList = mainCategoryService.getAllMainCategories();
            return ResponseEntity.ok(allMainCategoriesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path="{mainVCategoryId}")
    public ResponseEntity<MainCategory> getMainCategoryById(@PathVariable("mainVCategoryId") Long mainCategoryId){
        try {
            MainCategory mainCategoryById = mainCategoryService.getMainCategoryById(mainCategoryId);
            return ResponseEntity.ok(mainCategoryById);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //endregion
}
