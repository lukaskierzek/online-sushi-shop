package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = """
            SELECT
            	*
            FROM
            	ITEMS I
            	LEFT JOIN COMMENTS C ON C.COMMENTS_ID = I.ITEMS_COMMENTS_ID
            	LEFT JOIN MAIN_CATEGORIES MC ON MC.MAIN_CATEGORIES_ID = I.ITEMS_MAIN_CATEGORIES_ID
            WHERE
            	I.ITEMS_IS_HIDDEN = 0
            """, nativeQuery = true)
//    @Query(value = "SELECT * FROM Items i WHERE i.items_is_hidden = 0", nativeQuery = true)
    List<Item> findNonHiddenItems();
}
