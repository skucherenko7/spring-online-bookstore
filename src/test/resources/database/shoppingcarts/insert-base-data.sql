-- 1. Спочатку вставляємо користувача
INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'user111@example.com', 'Password111', 'User111', 'User111',
        '145 Main St, City, Country', false);

-- 2. Потім створюємо кошик — з тим самим id, що і user.id (через @MapsId)
INSERT INTO shopping_carts (id, is_deleted)
VALUES (1, false);

-- 3. Далі вставляємо книгу
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid',
        '978-1234567989', 520.00, 'Updated description',
        'https://example.com/updated-cover-image.jpg', false);

-- 4. І вже потім — позицію в кошику
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 2);

