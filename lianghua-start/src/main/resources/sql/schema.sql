-- 市场数据表
CREATE TABLE IF NOT EXISTS market_data
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol          VARCHAR(20)    NOT NULL COMMENT '股票代码',
    stock_name      VARCHAR(50)    COMMENT '股票名称',
    current_price   DECIMAL(12, 4) COMMENT '当前价格',
    open_price      DECIMAL(12, 4) COMMENT '开盘价',
    high_price      DECIMAL(12, 4) COMMENT '最高价',
    low_price       DECIMAL(12, 4) COMMENT '最低价',
    pre_close_price DECIMAL(12, 4) COMMENT '昨收价',
    volume          BIGINT         COMMENT '成交量（手）',
    turnover        DECIMAL(20, 4) COMMENT '成交额（元）',
    change_percent  DECIMAL(8, 4)  COMMENT '涨跌幅（%）',
    change_amount   DECIMAL(12, 4) COMMENT '涨跌额',
    turnover_rate   DECIMAL(8, 4)  COMMENT '换手率（%）',
    pe_ratia_ttm    DECIMAL(12, 4) COMMENT '市盈率（TTM）',
    pb_ratio        DECIMAL(12, 4) COMMENT '市净率',
    market_cap      DECIMAL(24, 4) COMMENT '市值（元）',
    data_time       TIMESTAMP      COMMENT '数据时间',
    data_source     VARCHAR(50)    COMMENT '数据来源',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_market_data_symbol ON market_data (symbol);

-- K线数据表
CREATE TABLE IF NOT EXISTS kline_data
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol         VARCHAR(20)    NOT NULL COMMENT '股票代码',
    trade_date     DATE           NOT NULL COMMENT '交易日期',
    open_price     DECIMAL(12, 4) COMMENT '开盘价',
    high_price     DECIMAL(12, 4) COMMENT '最高价',
    low_price      DECIMAL(12, 4) COMMENT '最低价',
    close_price    DECIMAL(12, 4) COMMENT '收盘价',
    volume         BIGINT         COMMENT '成交量（手）',
    turnover       DECIMAL(20, 4) COMMENT '成交额（元）',
    change_percent DECIMAL(8, 4)  COMMENT '涨跌幅（%）',
    turnover_rate  DECIMAL(8, 4)  COMMENT '换手率（%）',
    adjust_factor  DECIMAL(12, 6) COMMENT '复权因子',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_kline_symbol_date ON kline_data (symbol, trade_date);

-- 量化分析报告表
CREATE TABLE IF NOT EXISTS analysis_report
(
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_id                 VARCHAR(64)    NOT NULL COMMENT '报告唯一ID',
    symbol                    VARCHAR(20)    NOT NULL COMMENT '股票代码',
    stock_name                VARCHAR(50)    COMMENT '股票名称',
    ai_provider               VARCHAR(20)    COMMENT 'AI提供商（ALIYUN/BYTEDANCE）',
    ai_model                  VARCHAR(50)    COMMENT 'AI模型名称',
    overall_score             INT            COMMENT '综合评分（0-100）',
    recommendation            VARCHAR(10)    COMMENT '投资建议（BUY/HOLD/SELL）',
    target_price              DECIMAL(12, 4) COMMENT '目标价格',
    technical_summary         TEXT           COMMENT '技术面分析摘要',
    fundamental_summary       TEXT           COMMENT '基本面分析摘要',
    ai_analysis_content       TEXT           COMMENT 'AI分析全文',
    technical_indicators_json TEXT           COMMENT '技术指标JSON',
    risk_warnings_json        TEXT           COMMENT '风险提示JSON',
    created_at                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_report_symbol ON analysis_report (symbol);
CREATE INDEX IF NOT EXISTS idx_report_id ON analysis_report (report_id);
