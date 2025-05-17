--liquibase formatted sql

--changeset dolgaia:3

CREATE SEQUENCE product_seq START WITH 3 INCREMENT BY 1;

--rollback DROP SEQUENCE product_seq;