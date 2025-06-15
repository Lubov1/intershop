--liquibase formatted sql
--changeset dolgaia:4
INSERT INTO BASKETITEM (QUANTITY, product_id, user_name) VALUES
                                                  (2, 1, 'first'),
                                                  (1, 2, 'first'),
                                                  (3, 3, 'first');

INSERT INTO ORDERS (PRICE, user_name) VALUES
                               (30.04, 'first'),
                               (40.08, 'first');

INSERT INTO PRODUCTORDER (QUANTITY, order_id, product_id) VALUES
                                                              (2, 1, 1),
                                                              (1, 1, 2),
                                                              (3, 2, 3),
                                                              (1, 2, 4);

--rollback DELETE * FROM BASKETITEM;
--rollback DELETE * FROM ORDERS;
--rollback DELETE * FROM PRODUCTORDER;