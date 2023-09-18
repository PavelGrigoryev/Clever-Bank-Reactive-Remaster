--liquibase formatted sql

--changeset Grigoryev_Pavel:1
CREATE TABLE IF NOT EXISTS bank
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200)       NOT NULL,
    address      VARCHAR(200)       NOT NULL,
    phone_number VARCHAR(40) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "user"
(
    id            BIGSERIAL PRIMARY KEY,
    lastname      VARCHAR(40)        NOT NULL,
    firstname     VARCHAR(40)        NOT NULL,
    surname       VARCHAR(40)        NOT NULL,
    register_date DATE               NOT NULL,
    mobile_number varchar(40) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS account
(
    id           VARCHAR(40) PRIMARY KEY       NOT NULL,
    currency     VARCHAR(20)                   NOT NULL,
    balance      NUMERIC                       NOT NULL,
    opening_date DATE                          NOT NULL,
    closing_date DATE,
    bank_id      BIGINT REFERENCES bank (id)   NOT NULL,
    user_id      BIGINT REFERENCES "user" (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS transaction
(
    id                   BIGSERIAL PRIMARY KEY,
    date                 DATE        NOT NULL,
    time                 TIME        NOT NULL,
    type                 VARCHAR(20) NOT NULL,
    bank_sender_id       BIGINT      NOT NULL,
    bank_recipient_id    BIGINT      NOT NULL,
    account_sender_id    VARCHAR(40) NOT NULL,
    account_recipient_id VARCHAR(40) NOT NULL,
    sum                  NUMERIC     NOT NULL
);

CREATE TABLE IF NOT EXISTS byn_currency
(
    id          BIGSERIAL PRIMARY KEY,
    currency_id INT     NOT NULL,
    currency    VARCHAR NOT NULL,
    scale       INT     NOT NULL,
    rate        NUMERIC NOT NULL,
    update_date DATE    NOT NULL
);
