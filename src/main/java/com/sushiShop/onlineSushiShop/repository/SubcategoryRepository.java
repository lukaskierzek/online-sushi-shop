package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    @Query(value = """
            SELECT * FROM subcategories WHERE subcategories_is_hidden = 0
            """, nativeQuery = true)
    List<Subcategory> findAllNonHiddenSubcategories();

    @Query(value = """
            SELECT * FROM subcategories WHERE subcategories_id = :subcategoryId
            """, nativeQuery = true)
    Optional<Subcategory> findSubcategoryById(@Param("subcategoryId") Long subcategoryId);
}
