drop table if exists BASKETITEM;
drop table if exists PRODUCTORDER;
drop table if exists ORDERS;
drop table if exists PRODUCT;

CREATE TABLE if not exists PRODUCT(
                                      ID serial primary key ,
                                      NAME VARCHAR(255) NOT NULL,
                                      DESCRIPTION VARCHAR(255) NOT NULL,
                                      PRICE DECIMAL(10,2) NOT NULL,
                                      IMAGE bytea
);
CREATE TABLE if not exists BASKETITEM(
                                         QUANTITY INT NOT NULL,
                                         product_id bigint primary key,
                                         FOREIGN KEY (product_id) REFERENCES PRODUCT(ID)
);
CREATE TABLE if not exists ORDERS(
                                     ID serial primary key ,
                                     PRICE DECIMAL(10,2) NOT NULL
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

INSERT INTO BASKETITEM (QUANTITY, product_id) VALUES
                                                  (2, 1),
                                                  (1, 2),
                                                  (3, 3);

INSERT INTO ORDERS (PRICE) VALUES
                               (30.04),
                               (40.08);

INSERT INTO PRODUCTORDER (QUANTITY, order_id, product_id) VALUES
                                                              (2, 1, 1),
                                                              (1, 1, 2),
                                                              (3, 2, 3),
                                                              (1, 2, 4);
