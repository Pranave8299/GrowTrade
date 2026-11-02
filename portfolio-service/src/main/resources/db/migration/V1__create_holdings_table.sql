
CREATE SCHEMA IF NOT EXISTS portfolio;

CREATE TABLE portfolio.holdings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    average_price NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_holdings_user_symbol
        UNIQUE (user_id, symbol),

    CONSTRAINT chk_holdings_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_holdings_average_price
        CHECK (average_price >= 0)
);

CREATE INDEX idx_holdings_user_id
    ON portfolio.holdings(user_id);
