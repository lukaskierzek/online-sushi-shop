package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.component.CreateViewRunner;
import com.sushiShop.onlineSushiShop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Path to the created view ITEM_NON_HIDDEN.
 * <br>
 * See {@link CreateViewRunner#run(String...)}
 */

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = """
            SELECT
                *
            FROM
                ITEM_NON_HIDDEN
            WHERE
                MAIN_CATEGORIES_NAME = :mainCategoryName
        """, nativeQuery = true)
    List<Item> findNonHiddenItemsByCategory(@Param("mainCategoryName") String mainCategoryName);

    @Query(value = """
        SELECT
        	*
        FROM
        	ITEM_NON_HIDDEN
        """, nativeQuery = true)
//    @Query(value = "SELECT * FROM Items i WHERE i.items_is_hidden = 0", nativeQuery = true)
    List<Item> findNonHiddenItems();

    @Query(value = """
        SELECT
        	*
        FROM
        	ITEM_NON_HIDDEN
        WHERE
        	ITEMS_ID = :itemId
        """, nativeQuery = true)
    Optional<Item> findNonHiddenItem(@Param("itemId") Long itemId);

    @Query(value = """
        SELECT
        	*
        FROM
        	ITEM_HIDDEN_AND_NON_HIDDEN
        WHERE
        	ITEMS_ID = :itemId
        """, nativeQuery = true)
    Optional<Item> findItemById(@Param("itemId") Long itemId);

    @Query(value = """
        WITH
        	CTE_ITEMS_WITH_SUBCATEGORIES AS (
        		SELECT
        			I.ITEMS_ID,
        			STRING_AGG(SC.SUBCATEGORIES_NAME, ', ') ITEMS_SUBCATEGORIES
        		FROM
        			ITEMS I
        			LEFT JOIN ITEM_SUBCATEGORY I_S ON I.ITEMS_ID = I_S.ITEMS_ID
        			LEFT JOIN SUBCATEGORIES SC ON I_S.SUBCATEGORIES_ID = SC.SUBCATEGORIES_ID
        		GROUP BY
        			I.ITEMS_ID
        	)
        SELECT
        	I_N_H.*,
        	CTE.ITEMS_SUBCATEGORIES
        FROM
        	ITEM_NON_HIDDEN I_N_H
        	LEFT JOIN CTE_ITEMS_WITH_SUBCATEGORIES CTE ON CTE.ITEMS_ID = I_N_H.ITEMS_ID
        WHERE
        	CTE.ITEMS_SUBCATEGORIES LIKE '%NEW ITEM%'
        """, nativeQuery = true)
    List<Item> findNonHiddenItemsByNewItemCategory();
}
