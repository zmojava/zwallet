--liquibase formatted sql

--changeset ZMO:1
CREATE TABLE IF NOT EXISTS wallet
(
    id      UUID PRIMARY KEY,
    balance BIGINT NOT NULL
);

--changeset ZMO:2
CREATE INDEX idx_wallet_id ON wallet (id);


