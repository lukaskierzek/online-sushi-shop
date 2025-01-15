package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT * FROM Items i LEFT JOIN comments c on c.comments_id = i.items_comments_id WHERE i.items_is_hidden = 0", nativeQuery = true)
    List<Item> findNonHiddenItems();
}
