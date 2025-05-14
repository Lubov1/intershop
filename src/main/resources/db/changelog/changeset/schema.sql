--liquibase formatted sql

--changeset dolgaia:1

CREATE TABLE if not exists PRODUCT(
    ID serial primary key ,
    NAME VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(255) NOT NULL,
    PRICE DECIMAL(10,2) NOT NULL,
    IMAGE bytea
);
CREATE TABLE if not exists BASKETITEM(
                                       ID serial primary key,
                                       QUANTITY INT NOT NULL,
                                        product_id BIGINT NOT NULL,
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

--rollback drop table PRODUCT;
--rollback drop table BASKETITEM;
--rollback drop table PRODUCTORDER;
--rollback drop table ORDERS;
