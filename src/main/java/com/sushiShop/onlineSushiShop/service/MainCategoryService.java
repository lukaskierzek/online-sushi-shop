package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.repository.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainCategoryService {
    private final MainCategoryRepository mainCategoryRepository;

    @Autowired
    public MainCategoryService(MainCategoryRepository mainCategoryRepository) {
        this.mainCategoryRepository = mainCategoryRepository;
    }

    public MainCategory getMainCategoryById(Long mainCategoryId) {
        return mainCategoryRepository.findMainCategoryById(mainCategoryId)
                .orElseThrow(() -> new IllegalStateException(String.format("Main category by %s was not found", mainCategoryId)));
    }
}
