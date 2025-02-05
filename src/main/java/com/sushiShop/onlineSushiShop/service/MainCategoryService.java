package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.exception.MainCategoryNotFoundException;
import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.repository.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainCategoryService {
    private final MainCategoryRepository mainCategoryRepository;

    @Autowired
    public MainCategoryService(MainCategoryRepository mainCategoryRepository) {
        this.mainCategoryRepository = mainCategoryRepository;
    }

    public MainCategory getMainCategoryById(Long mainCategoryId) {
        return mainCategoryRepository.findMainCategoryById(mainCategoryId)
            .orElseThrow(() -> new MainCategoryNotFoundException(String.format("Main category with ID %s was not found", mainCategoryId)));
    }

    public List<MainCategory> getAllMainCategories() {
        return mainCategoryRepository.findAll();
    }

    public MainCategory postMainCategory(MainCategory mainCategory) {
        return mainCategoryRepository.save(mainCategory);
    }

    public List<MainCategory> getNonHiddenMainCategories() {
        return mainCategoryRepository.findNonHiddenMainCategories();
    }

    public MainCategory putMainCategory(MainCategory mainCategoryById) {
        return postMainCategory(mainCategoryById);
    }
}
