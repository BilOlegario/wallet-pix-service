CREATE TABLE pix_transfers (
    id UUID PRIMARY KEY,
    end_to_end_id VARCHAR(255) NOT NULL UNIQUE,
    sender_wallet_id UUID NOT NULL,
    receiver_key_type VARCHAR(50) NOT NULL,
    receiver_key_value VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_sender_wallet FOREIGN KEY (sender_wallet_id) REFERENCES wallets(id)
);

CREATE TABLE idempotency (
    id BIGSERIAL PRIMARY KEY,
    scope VARCHAR(100) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (scope, idempotency_key)
);
