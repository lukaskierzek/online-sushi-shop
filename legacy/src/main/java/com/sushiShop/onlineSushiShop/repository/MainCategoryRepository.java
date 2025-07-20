package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

    @Query(value = """
        SELECT
            *
        FROM
            main_categories
        WHERE
            main_categories_id = :mainCategoryId
        """, nativeQuery = true)
    Optional<MainCategory> findMainCategoryById(@Param("mainCategoryId") Long mainCategoryId);

    @Query(value = """
        SELECT
        	*
        FROM
        	MAIN_CATEGORIES
        WHERE
        	MAIN_CATEGORIES_IS_HIDDEN = 0
        """, nativeQuery = true)
    List<MainCategory> findNonHiddenMainCategories();
}
