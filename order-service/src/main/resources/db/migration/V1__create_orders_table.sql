
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side VARCHAR(4) NOT NULL
        CHECK (side IN ('BUY', 'SELL')),
    quantity INTEGER NOT NULL
        CHECK (quantity > 0),
    status VARCHAR(20) NOT NULL
        CHECK (status IN (
            'PENDING', 'PROCESSING', 'FILLED',
            'REJECTED', 'CANCELLED'
        )),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id
    ON orders(user_id);
