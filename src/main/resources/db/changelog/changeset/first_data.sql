--liquibase formatted sql
--changeset dolgaia:1
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Первый продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Второй продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Третий продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Четвертый продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Пятый продукт', 'Описание', 10.02, NULL);
insert into PRODUCT (name, DESCRIPTION, PRICE, image) values ('Шестой продукт', 'Описание', 10.02, NULL);

--rollback DELETE * FROM PRODUCT;