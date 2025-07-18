package com.sushiShop.onlineSushiShop;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import com.sushiShop.onlineSushiShop.model.AdditionalInformation;
import com.sushiShop.onlineSushiShop.model.Comment;
import com.sushiShop.onlineSushiShop.model.Item;
import com.sushiShop.onlineSushiShop.model.MainCategory;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class MockItems {
    public static List<Item> getItems() {
        AdditionalInformation additionalInformationIsHiddenNo1 = new AdditionalInformation(
            IsHidden.NO
        );

        AdditionalInformation additionalInformationIsHiddenYes1 = new AdditionalInformation(
            IsHidden.YES
        );

        AdditionalInformation additionalInformationIsHiddenNo2 = new AdditionalInformation(
            IsHidden.NO
        );

        Comment comment1 = new Comment(
            "Comment text 1",
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            additionalInformationIsHiddenNo1
        );

        Comment comment2 = new Comment(
            null,
            "Comment text 2",
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            null,
            additionalInformationIsHiddenYes1
        );

        Comment comment3 = new Comment(
            null,
            "Comment text 3",
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
            null,
            additionalInformationIsHiddenNo2
        );

        MainCategory mainCategoryOne = new MainCategory(null, "Main Subcategory 1", additionalInformationIsHiddenNo1);
        MainCategory mainCategoryTwo = new MainCategory(null, "Main Subcategory 2", additionalInformationIsHiddenNo1);

        return List.of(
            new Item(null,
                "Rools1",
                20,
                10,
                "http",
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                additionalInformationIsHiddenNo1,
                comment1,
                mainCategoryOne),
            new Item(null,
                "Rools2",
                20,
                10,
                "http",
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                additionalInformationIsHiddenYes1,
                comment2,
                mainCategoryTwo),
            new Item(null,
                "Rools3",
                20,
                10,
                "http",
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                LocalDateTime.of(2023, Month.JANUARY, 10, 0, 0, 0, 0),
                additionalInformationIsHiddenNo2,
                comment3,
                mainCategoryOne)
        );
    }
}
