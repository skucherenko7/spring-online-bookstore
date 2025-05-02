DELETE FROM books_categories
WHERE book_id IN (SELECT id FROM books
                  WHERE title = 'Seven Husbands of Evelyn Hugo'
                    AND author = 'Taylor Jenkins Reid'
                    AND isbn = '978-1234567989'
                    AND price = 520.00);
DELETE FROM books
WHERE title = 'Lisova pisnya'
  AND author = 'Ukrainka'
  AND isbn = '978-1234567979'
  AND price = 670.00;
