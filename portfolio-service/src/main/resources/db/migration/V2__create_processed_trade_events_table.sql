CREATE TABLE portfolio.processed_trade_events (
    execution_id UUID PRIMARY KEY,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_processed_trade_events_processed_at
    ON portfolio.processed_trade_events(processed_at);