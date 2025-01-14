package com.sushiShop.onlineSushiShop.repository;

import com.sushiShop.onlineSushiShop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT * FROM Items WHERE Items_is_hidden = 0", nativeQuery = true)
//    @Query("SELECT i FROM Item i WHERE i.additionalInformation.isHidden = 0")
    List<Item> findNonHiddenItems();
}
