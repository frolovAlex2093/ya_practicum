-- Пост 1
INSERT INTO posts (title, text, likes_count)
VALUES ('Путешествие на Марс', 'Марс — четвёртая по удалённости от Солнца и седьмая по размеру планета Солнечной системы.', 5);

INSERT INTO post_tags (post_id, tag) VALUES (1, 'космос');
INSERT INTO post_tags (post_id, tag) VALUES (1, 'марс');

INSERT INTO comments (post_id, text) VALUES (1, 'Круто! Хочу туда.');
INSERT INTO comments (post_id, text) VALUES (1, 'А Илон Маск уже там?');

-- Пост 2
INSERT INTO posts (title, text, likes_count)
VALUES ('Рецепт пиццы', 'Тесто, томатный соус, сыр моцарелла, базилик. Выпекать при 250 градусах.', 12);

INSERT INTO post_tags (post_id, tag) VALUES (2, 'еда');
INSERT INTO post_tags (post_id, tag) VALUES (2, 'рецепты');

-- Пост 3 (без тегов и комментов)
INSERT INTO posts (title, text, likes_count)
VALUES ('Заметка о Java', 'Java — строго типизированный объектно-ориентированный язык программирования.', 3);