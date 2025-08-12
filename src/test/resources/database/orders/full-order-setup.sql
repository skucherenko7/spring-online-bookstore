INSERT INTO categories (id, name, description, is_deleted)
VALUES
    (1, 'Poetry', 'Poetry books', FALSE),
    (2, 'Historical', 'Historical books', FALSE),
    (3, 'Story', 'Story books', FALSE),
    (4, 'Fantasy', 'Fantasy books', FALSE),
    (5, 'Novel', 'Women''s novels', FALSE),
    (6, 'Detective', 'Detective books', FALSE);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES (1, 'user@util.com', 'password', 'Test', 'User', FALSE);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '978-1234567989', 520.00, 'Novel', 'https://example.com/updated-cover-image.jpg', FALSE),
    (2, 'Lisova pisnya', 'Ukrainka', '978-1234567979', 670.00, 'Fantasy', 'https://example.com/updated-cover-image.jpg', FALSE),
    (3, 'Marusya Churai', 'Kostenko', '978-1234563329', 620.00, 'Historical books', 'https://example.com/updated-cover-image.jpg', FALSE);

INSERT INTO books_categories (book_id, category_id)
VALUES
    (1, 5),
    (1, 6),
    (2, 1),
    (2, 4),
    (3, 2);

INSERT INTO shopping_carts (id, is_deleted)
VALUES (1, FALSE);

INSERT INTO cart_items (id, book_id, shopping_cart_id, quantity)
VALUES (1, 3, 1, 2);

INSERT INTO orders (id, status, total, order_date, shipping_address, user_id)
VALUES (1, 'PENDING', 1240.00, '2025-04-04 11:52:36', '123 Test St, Test City', 1);

INSERT INTO order_items (id, order_id, book_id, quantity, price)
VALUES (1, 1, 3, 2, 620.00);
