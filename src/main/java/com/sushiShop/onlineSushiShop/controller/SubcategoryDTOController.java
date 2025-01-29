package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.Subcategory;
import com.sushiShop.onlineSushiShop.model.dto.SubcategoryDTO;
import com.sushiShop.onlineSushiShop.model.dto.SubcategoryPostDTO;
import com.sushiShop.onlineSushiShop.service.SubcategoryDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/onlinesushishop/subcategory")
public class SubcategoryDTOController {
    private final SubcategoryDTOService subcategoryDTOService;

    @Autowired
    public SubcategoryDTOController(SubcategoryDTOService subcategoryDTOService) {
        this.subcategoryDTOService = subcategoryDTOService;
    }

    //region Get
    @GetMapping()
    public ResponseEntity<List<SubcategoryDTO>> getAllSubcategoriesDTO() {
        try {
            List<SubcategoryDTO> subcategoryDTOList = subcategoryDTOService.getAllSubcategoriesDTO();
            return ResponseEntity.ok(subcategoryDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping(path = "non-hidden")
    public ResponseEntity<List<SubcategoryDTO>> getAllNonHiddenSubcategoriesDTO() {
        try {
            List<SubcategoryDTO> nonHiddenSubcategoriesList = subcategoryDTOService.getAllNonHiddenSubcategoriesDTO();
            return ResponseEntity.ok(nonHiddenSubcategoriesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    //endregion

    //region Post
    @PostMapping()
    public ResponseEntity<?> postSubcategoryDTO(@RequestBody SubcategoryPostDTO subcategoryPostDTO, UriComponentsBuilder uriComponentsBuilder) {
        try {
            Subcategory subcategoryCreated = subcategoryDTOService.postSubcategoryFromSubcategoryPostDTO(subcategoryPostDTO);
            return ResponseEntity.created(
                    uriComponentsBuilder.path("api/v1/onlinesushishop/raw/subcategory/{subcategoryId}")
                            .buildAndExpand(subcategoryCreated.getSubcategoryId())
                            .toUri()
            ).body(subcategoryCreated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error creating item " + subcategoryPostDTO.subcategoryName(),
                    "message", e.getMessage()
            ));
        }

    }
    //endregion
}
