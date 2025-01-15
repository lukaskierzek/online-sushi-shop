package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Comment;
import com.sushiShop.onlineSushiShop.model.Item;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class MockItems {
    public static List<Item> getItems() {
        AdditionalInformation additionalInformationIsHiddenNo1 = new AdditionalInformation(
                IsHidden.NO,
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0)
        );

        AdditionalInformation additionalInformationIsHiddenYes1 = new AdditionalInformation(
                IsHidden.YES,
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0)
        );

        AdditionalInformation additionalInformationIsHiddenNo2 = new AdditionalInformation(
                IsHidden.NO,
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0)
        );

        Comment comment1 = new Comment(
                "Comment text 1", additionalInformationIsHiddenNo1
        );

        Comment comment2 = new Comment(
                null, "Comment text 2", null, additionalInformationIsHiddenYes1
        );

        Comment comment3 = new Comment(
                null, "Comment text 3", null, additionalInformationIsHiddenNo2
        );

        return List.of(
                new Item(null, "Rools1", 20, 10, "http", additionalInformationIsHiddenNo1, comment1),
                new Item(null, "Rools2", 20, 10, "http", additionalInformationIsHiddenYes1, comment2),
                new Item(null, "Rools3", 20, 10, "http", additionalInformationIsHiddenNo2, comment3)
        );
    }
}
