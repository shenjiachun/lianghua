package com.lianghua.domain.market.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * K线数据实体（日K线）
 */
@Data
public class KLineData {

    private Long id;

    /** 股票代码 */
    private String symbol;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** 开盘价 */
    private BigDecimal openPrice;

    /** 最高价 */
    private BigDecimal highPrice;

    /** 最低价 */
    private BigDecimal lowPrice;

    /** 收盘价 */
    private BigDecimal closePrice;

    /** 成交量（手） */
    private Long volume;

    /** 成交额（元） */
    private BigDecimal turnover;

    /** 涨跌幅（%） */
    private BigDecimal changePercent;

    /** 换手率（%） */
    private BigDecimal turnoverRate;

    /** 复权因子 */
    private BigDecimal adjustFactor;
}
