drop table if exists BASKETITEM;
drop table if exists PRODUCTORDER;
drop table if exists ORDERS;
drop table if exists PRODUCT;
drop table if exists USERS;

CREATE TABLE if not exists PRODUCT(
                                      ID serial primary key ,
                                      NAME VARCHAR(255) NOT NULL,
                                      DESCRIPTION VARCHAR(255) NOT NULL,
                                      PRICE DECIMAL(10,2) NOT NULL,
                                      IMAGE bytea
);

CREATE TABLE if not exists USERS(
                                    ID serial primary key ,
                                    username VARCHAR(255) UNIQUE NOT NULL,
                                    password VARCHAR(255) NOT NULL,
                                    authority VARCHAR(255)
);

CREATE TABLE if not exists BASKETITEM(
                                         QUANTITY INT NOT NULL,
                                         product_id bigint NOT NULL,
                                         user_name VARCHAR(255) NOT NULL,
                                         PRIMARY KEY (user_name, product_id),
                                         FOREIGN KEY (product_id) REFERENCES PRODUCT(ID),
                                         FOREIGN KEY (user_name) REFERENCES USERS(username)
);
CREATE TABLE if not exists ORDERS(
                                     ID serial primary key ,
                                     PRICE DECIMAL(10,2) NOT NULL,
                                     user_name VARCHAR(255) NOT NULL,
                                     FOREIGN KEY (user_name) REFERENCES USERS(username)
);
CREATE TABLE if not exists PRODUCTORDER(
                                           QUANTITY INT NOT NULL,
                                           order_id BIGINT NOT NULL ,
                                           product_id BIGINT NOT NULL,
                                           PRIMARY KEY(order_id, product_id),
                                           FOREIGN KEY (order_id) REFERENCES ORDERS(ID),
                                           FOREIGN KEY (product_id) REFERENCES PRODUCT(ID)
);

insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Первый продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Второй продукт', 'Описание', 11.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Третий продукт', 'Описание', 12.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Четвертый продукт', 'Описание', 5.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Пятый продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Шестой продукт', 'Описание', 10.02, NULL);

INSERT INTO USERS (username, password, authority) VALUES
                                                      ('user1', '$2a$10$4S95ssTOU0Uj1mq/q3xFxutLT0RCj0JoFSua06/9Z8pE73nsakqG6', 'ROLE_USER'),
                                                      ('admin', '$2a$10$4S95ssTOU0Uj1mq/q3xFxutLT0RCj0JoFSua06/9Z8pE73nsakqG6', 'ROLE_ADMIN');

INSERT INTO BASKETITEM (QUANTITY, product_id, user_name) VALUES
                                                             (2, 1, 'user1'),
                                                             (1, 2, 'user1'),
                                                             (3, 3, 'user1');

INSERT INTO ORDERS (PRICE, user_name) VALUES
                                          (30.04, 'user1'),
                                          (40.08, 'user1');

INSERT INTO PRODUCTORDER (QUANTITY, order_id, product_id) VALUES
                                                              (2, 1, 1),
                                                              (1, 1, 2),
                                                              (3, 2, 3),
                                                              (1, 2, 4);
