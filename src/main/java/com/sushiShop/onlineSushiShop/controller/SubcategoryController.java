package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.Subcategory;
import com.sushiShop.onlineSushiShop.service.SubcategoryService;
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
@RequestMapping(path = "api/v1/onlinesushishop/raw/subcategory")
public class SubcategoryController {
    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @GetMapping()
    public ResponseEntity<List<Subcategory>> getAllSubcategories() {
        try {
            List<Subcategory> allSubcategories = subcategoryService.getAllSubcategories();
            return ResponseEntity.ok(allSubcategories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "{subcategoryId}")
    public ResponseEntity<Subcategory> getSubcategoryById(@PathVariable("subcategoryId") Long subcategoryId) {
        try {
            Subcategory subcategoryById = subcategoryService.getSubcategoryById(subcategoryId);
            return ResponseEntity.ok(subcategoryById);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
