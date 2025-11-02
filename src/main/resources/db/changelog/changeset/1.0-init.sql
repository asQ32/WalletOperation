CREATE TABLE wallet
(
    id         UUID PRIMARY KEY,
    balance    NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP      NOT NULL
);

CREATE TABLE wallet_operation
(
    id             UUID PRIMARY KEY,
    wallet_id      UUID           NOT NULL,
    operation_type VARCHAR(10)    NOT NULL,
    amount         NUMERIC(19, 2) NOT NULL,
    created_at     TIMESTAMP      NOT NULL,
    CONSTRAINT fk_wallet_operation_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallet (id)
);
