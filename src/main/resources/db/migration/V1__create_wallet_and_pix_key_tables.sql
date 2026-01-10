CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    balance DECIMAL(19, 2) NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE pix_keys (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    key_type VARCHAR(20) NOT NULL,
    key_value VARCHAR(255) NOT NULL UNIQUE
);
