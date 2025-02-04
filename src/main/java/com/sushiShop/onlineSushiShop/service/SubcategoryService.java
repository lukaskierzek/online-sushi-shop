package com.sushiShop.onlineSushiShop.service;

import com.sushiShop.onlineSushiShop.exception.SubcategoryNotFoundException;
import com.sushiShop.onlineSushiShop.model.Subcategory;
import com.sushiShop.onlineSushiShop.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoryService {
    private final SubcategoryRepository subcategoryRepository;

    @Autowired
    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    public List<Subcategory> getAllSubcategories() {
        return subcategoryRepository.findAll();
    }

    public List<Subcategory> getAllNonHiddenSubcategories() {
        return subcategoryRepository.findAllNonHiddenSubcategories();
    }

    public Subcategory postSubcategory(Subcategory subcategory) {
        return subcategoryRepository.save(subcategory);
    }

    public Subcategory getSubcategoryById(Long subcategoryId) {
        return subcategoryRepository.findSubcategoryById(subcategoryId)
                .orElseThrow(() -> new SubcategoryNotFoundException(String.format("Subcategory by %s was not found", subcategoryId)));
    }
}
