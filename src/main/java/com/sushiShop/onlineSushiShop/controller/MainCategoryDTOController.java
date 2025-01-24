package com.sushiShop.onlineSushiShop.controller;

import com.sushiShop.onlineSushiShop.model.MainCategory;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryDTO;
import com.sushiShop.onlineSushiShop.model.dto.MainCategoryPostDTO;
import com.sushiShop.onlineSushiShop.service.MainCategoryDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @GetMapping(path = "non-hidden")
    public ResponseEntity<List<MainCategoryDTO>> getNonHiddenItemsDTO() {
        try {
            List<MainCategoryDTO> nonHiddenMainCategoriesList = mainCategoryDTOService.getNonHiddenMainCategoriesDTO();
            return ResponseEntity.ok(nonHiddenMainCategoriesList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }
    //endregion

    //region Post
    @PostMapping()
    public ResponseEntity<?> postManCategoryDTO(@RequestBody MainCategoryPostDTO mainCategoryPostDTO, UriComponentsBuilder uriComponentsBuilder) {
        try {
            MainCategory mainCategoryCreated = mainCategoryDTOService.postNewMainCategoryFromMainCategoryDTO(mainCategoryPostDTO);
            return ResponseEntity.created(
                    uriComponentsBuilder.path("api/v1/onlinesushishop/raw/main-category/{mainCategoryId}")
                            .buildAndExpand(mainCategoryCreated.getMainCategoryId())
                            .toUri()
            ).body(mainCategoryCreated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Error creating main category " + mainCategoryPostDTO.mainCategoryName(),
                    "message", e.getMessage()
            ));
        }
    }
    //endregion
}
