CREATE TABLE trade_execution.trade_executions
(
    id              UUID PRIMARY KEY,
    order_id        UUID NOT NULL UNIQUE,
    symbol          VARCHAR(20) NOT NULL,
    side            VARCHAR(10) NOT NULL,
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    execution_price NUMERIC(19, 4) NOT NULL,
    total_amount    NUMERIC(19, 4) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    executed_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_trade_executions_executed_at
    ON trade_execution.trade_executions (executed_at);