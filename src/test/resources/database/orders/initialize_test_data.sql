DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM shopping_carts;

INSERT INTO categories (id, name, description, is_deleted)
VALUES
    (1, 'Poetry', 'Poetry books', FALSE),
    (2, 'Historical', 'Historical books', FALSE),
    (3, 'Story', 'Story books', FALSE),
    (4, 'Fantasy', 'Fantasy books', FALSE),
    (5, 'Novel', 'Women''s novels', FALSE),
    (6, 'Detective', 'Detective books', FALSE);

INSERT INTO users (id, email, password, first_name, last_name, is_deleted)
VALUES (1, 'user@example.com', 'password', 'Test', 'User', FALSE);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '978-1234567989', 520.00, 'Novel', 'https://example.com/updated-cover-image.jpg', FALSE),
    (2, 'Lisova pisnya', 'Ukrainka', '978-1234567979', 670.00, 'Fantasy', 'https://example.com/updated-cover-image.jpg', FALSE),
    (3, 'Marusya Churai', 'Kostenko', '978-1234563329', 620.00, 'Historical books', 'https://example.com/updated-cover-image.jpg', FALSE);

INSERT INTO shopping_carts (id, is_deleted)
VALUES (1, FALSE);

INSERT INTO cart_items (id, book_id, shopping_cart_id, quantity)
VALUES (1, 3, 1, 2);
