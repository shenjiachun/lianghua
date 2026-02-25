CREATE TABLE IF NOT EXISTS t_market_data (
    id VARCHAR(36) PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL,
    stock_name VARCHAR(100),
    open_price DECIMAL(10,4),
    close_price DECIMAL(10,4),
    high_price DECIMAL(10,4),
    low_price DECIMAL(10,4),
    volume BIGINT,
    amount DECIMAL(20,4),
    trade_date DATE NOT NULL,
    change_rate DECIMAL(8,4),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stock_date (stock_code, trade_date)
);

CREATE TABLE IF NOT EXISTS t_analysis_result (
    id VARCHAR(36) PRIMARY KEY,
    stock_code VARCHAR(20) NOT NULL,
    analysis_type VARCHAR(50) NOT NULL,
    ai_model VARCHAR(50) NOT NULL,
    prompt TEXT,
    content TEXT,
    suggestion VARCHAR(50),
    confidence_score DECIMAL(5,4),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_stock_type (stock_code, analysis_type)
);
