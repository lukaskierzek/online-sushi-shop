INSERT INTO comments(comments_id, comments_text, comments_created_at, comments_is_hidden, comments_update_at)
VALUES (1, 'comment text 1', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (2, 'comment text 2', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (3, 'comment text 3', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (4, 'comment text 3', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (5, 'comment text 4', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000');

INSERT INTO main_categories(main_categories_id, main_categories_created_at, main_categories_is_hidden,
                            main_categories_update_at, main_categories_name)
VALUES (1, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Main category 1'),
       (2, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Main category 2'),
       (3, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Main category 3'),
       (4, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Main category 4'),
       (5, '2025-01-22 12:44:00.000000', 1, '2025-01-22 12:44:00.000000', 'Main category 5');


INSERT INTO items(items_id, items_actual_price, items_image_url, items_name, items_old_price, items_created_at,
                  items_is_hidden, items_update_at, items_comments_id, items_main_categories_id)
VALUES (1, 10, 'http', 'Item1', 20, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 1, 1),
       (2, 10, 'http', 'Item2', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 2, 1),
       (3, 20, 'http', 'Item3', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 3, 2),
       (4, 20, 'http', 'Item3', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 4, 3),
       (5, 22, 'http', 'Item4', 33, '2025-01-22 12:44:00.000000', 1, '2025-01-22 12:44:00.000000', 5, 4);