--liquibase formatted sql
--changeset dolgaia:4
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

--rollback DELETE * FROM BASKETITEM;
--rollback DELETE * FROM ORDERS;
--rollback DELETE * FROM PRODUCTORDER;
--rollback DELETE * FROM USERS;