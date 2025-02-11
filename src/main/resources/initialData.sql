INSERT INTO comments(comments_id, comments_text, comments_created_at, comments_is_hidden, comments_updated_at)
VALUES (nextval('comment_sequence'), 'A piece of rice with vinegar, topped with a slice of raw tuna. It is one of the most popular sushi, often served with wasabi and soy sauce.', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'A type of uramaki (inverted roll) where the rice is on the outside and the nori is on the inside. It contains crab, avocado and cucumber. It is a popular sushi in the United States.', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'Thinly sliced slices of raw salmon, served without rice. Often served with wasabi, soy sauce and pickled ginger.', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'A hand-rolled cone-shaped sushi roll filled with rice, spicy tuna, cucumber and avocado. Nori is used to wrap the ingredients.', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'An inverted sushi roll, where the rice is on the outside and the nori is on the inside. It contains tempura prawn, avocado, cucumber and is wrapped with avocado slices on top, often served with unagi (eel sauce).', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'Sushi roll with cucumber', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000'),
       (nextval('comment_sequence'), 'Sushi roll with avocado', '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000');

INSERT INTO main_categories(main_categories_id, main_categories_created_at, main_categories_is_hidden,
                            main_categories_updated_at, main_categories_name)
VALUES (nextval('main_category_sequence'), '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Nigiri'),
       (nextval('main_category_sequence'), '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Maki'),
       (nextval('main_category_sequence'), '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Sashimi'),
       (nextval('main_category_sequence'), '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 'Temaki'),
       (nextval('main_category_sequence'), '2025-01-22 12:44:00.000000', 1, '2025-01-22 12:44:00.000000', 'Urakami');


INSERT INTO items(items_id, items_actual_price, items_image_url, items_name, items_old_price, items_created_at,
                  items_is_hidden, items_updated_at, items_comments_id, items_main_categories_id)
VALUES (nextval('item_sequence'), 10, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Maguro Nigiri', 20, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 1, 1),
       (nextval('item_sequence'), 10, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'California Roll', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 2, 1),
       (nextval('item_sequence'), 20, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Salmon Sashimi', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 3, 2),
       (nextval('item_sequence'), 20, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Spicy Tuna Temaki', 10, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 4, 3),
       (nextval('item_sequence'), 22, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Dragon Roll', 33, '2025-01-22 12:44:00.000000', 1, '2025-01-22 12:44:00.000000', 5, 4),
       (nextval('item_sequence'), 22, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Kappa Maki', 33, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 6, 2),
       (nextval('item_sequence'), 22, 'https://img.freepik.com/free-photo/freshness-plate-gourmet-seafood-maki-sushi-avocado-sashimi-generated-by-artificial-intelligence_25030-66337.jpg?w=1380&t=st=1709289227~exp=1709289827~hmac=b87b76aed267433a4760bd9582f1c8b8ca8564915573edeb67ebf356a0b3eb3f', 'Avocado Maki', 33, '2025-01-22 12:44:00.000000', 0, '2025-01-22 12:44:00.000000', 7, 2);

insert into subcategories(subcategories_id, subcategories_is_hidden, subcategories_created_at, subcategories_name, subcategories_updated_at)
VALUES
    (nextval('subcategory_sequence'), 0, '2025-01-22 12:44:00.000000', 'NEW ITEM', '2025-01-22 12:44:00.000000'),
    (nextval('subcategory_sequence'), 0, '2025-01-22 12:44:00.000000', 'VEGE', '2025-01-22 12:44:00.000000'),
    (nextval('subcategory_sequence'), 1, '2025-01-22 12:44:00.000000', '*', '2025-01-22 12:44:00.000000');


INSERT INTO item_subcategory(items_id, subcategories_id)
VALUES (1, 1),
       (1, 3),
       (6, 1),
       (6, 2),
       (7, 1),
       (7, 2),
       (7, 3);
