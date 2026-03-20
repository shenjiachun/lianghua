package com.lianghua.client.dto.market;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 市场数据传输对象
 */
@Data
public class MarketDataDTO {

    /** 股票代码 */
    private String symbol;

    /** 股票名称 */
    private String name;

    /** 当前价格 */
    private BigDecimal currentPrice;

    /** 开盘价 */
    private BigDecimal openPrice;

    /** 最高价 */
    private BigDecimal highPrice;

    /** 最低价 */
    private BigDecimal lowPrice;

    /** 昨收价 */
    private BigDecimal preClosePrice;

    /** 成交量（手） */
    private Long volume;

    /** 成交额（元） */
    private BigDecimal turnover;

    /** 涨跌幅（%） */
    private BigDecimal changePercent;

    /** 涨跌额 */
    private BigDecimal changeAmount;

    /** 换手率（%） */
    private BigDecimal turnoverRate;

    /** 市盈率（TTM） */
    private BigDecimal peRatiaTtm;

    /** 市净率 */
    private BigDecimal pbRatio;

    /** 市值（元） */
    private BigDecimal marketCap;

    /** 数据时间 */
    private LocalDateTime dataTime;

    /** 数据来源 */
    private String dataSource;
}
