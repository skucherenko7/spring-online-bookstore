SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM cart_items;
DELETE FROM shopping_carts;
DELETE FROM users WHERE email = 'user111@util.com';

SET FOREIGN_KEY_CHECKS = 1;
