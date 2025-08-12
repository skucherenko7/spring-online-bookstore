SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM cart_items;
DELETE FROM shopping_carts;
DELETE FROM users;
DELETE FROM books;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE shopping_carts AUTO_INCREMENT = 1;
ALTER TABLE cart_items AUTO_INCREMENT = 1;
ALTER TABLE books AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'user', 'Password111', 'User111', 'User111', '145 Main St, City, Country', false);

INSERT INTO shopping_carts (id, is_deleted)
VALUES (1, false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '978-1234567989', 520.00, 'Updated description', 'https://example.com/updated-cover-image.jpg', false);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 2);
