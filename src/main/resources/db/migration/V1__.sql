CREATE TABLE books
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    author        VARCHAR(255) NOT NULL,
    isbn          VARCHAR(255) NOT NULL,
    price         DECIMAL      NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    cover_image   VARCHAR(255) NULL,
    is_deleted    BIT(1)       NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

CREATE TABLE users
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    email            VARCHAR(255) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    first_name       VARCHAR(255) NOT NULL,
    last_name        VARCHAR(255) NOT NULL,
    shipping_address VARCHAR(255) NULL,
    is_deleted       BIT(1)       NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE books
    ADD CONSTRAINT uc_books_isbn UNIQUE (isbn);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);