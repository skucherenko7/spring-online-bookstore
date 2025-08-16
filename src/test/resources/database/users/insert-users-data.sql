SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'user111@util.com', 'Password111', 'User111', 'User111', '145 Main St, City, Country', false);
