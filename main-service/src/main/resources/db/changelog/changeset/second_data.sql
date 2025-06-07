--liquibase formatted sql
--changeset dolgaia:4
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

--rollback DELETE * FROM BASKETITEM;
--rollback DELETE * FROM ORDERS;
--rollback DELETE * FROM PRODUCTORDER;