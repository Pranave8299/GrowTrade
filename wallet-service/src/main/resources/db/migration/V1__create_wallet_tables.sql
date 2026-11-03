CREATE TABLE wallet.wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT chk_wallet_balance CHECK (balance >= 0)
);

CREATE TABLE wallet.wallet_transactions (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    reference_id UUID,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_wallet_transaction_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallet.wallets(id),

    CONSTRAINT chk_wallet_transaction_amount
        CHECK (amount > 0),

    CONSTRAINT chk_wallet_transaction_type
        CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'BUY', 'SELL'))
);

CREATE INDEX idx_wallet_transactions_wallet_id
ON wallet.wallet_transactions(wallet_id);

CREATE INDEX idx_wallet_transactions_created_at
ON wallet.wallet_transactions(created_at);