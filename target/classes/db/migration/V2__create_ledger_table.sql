CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    amount DECIMAL(19, 2) NOT NULL,
    entry_type VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_ledger_wallet_id ON ledger_entries(wallet_id);
