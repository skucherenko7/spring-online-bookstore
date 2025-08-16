INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'Seven Husbands of Evelyn Hugo', 'Taylor Jenkins Reid', '978-1234567989', 520.00, 'Updated description',
     'https://example.com/updated-cover-image.jpg', FALSE),
    (2,'Lisova pisnya', 'Ukrainka', '978-1234567979', 670.00, 'Updated description',
     'https://example.com/updated-cover-image.jpg', FALSE);

INSERT INTO books_categories (book_id, category_id)
VALUES
    (1, 3),
    (1, 5),
    (2, 1),
    (2, 4);
