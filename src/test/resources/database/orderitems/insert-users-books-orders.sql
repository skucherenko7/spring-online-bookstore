INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'user111@util.com', 'Password111', 'User111', 'User111', '145 Main St, City, Country', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '978-1234567989', 520.00, 'Updated description', 'https://example.com/updated-cover-image.jpg', 0),
    (2, 'Lisova pisnya', 'Ukrainka', '978-1234567979', 670.00, 'Updated description', 'https://example.com/updated-cover-image.jpg', 0);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address)
VALUES (1, 1, 'PENDING', 1190.00, '2025-04-04 11:52:36', '145 Main St, City, Country');
