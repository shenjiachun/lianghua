package com.lianghua.domain.market.model.entity;

import com.lianghua.domain.market.model.vo.StockSymbol;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 市场数据领域实体
 */
@Data
public class MarketData {

    private Long id;

    /** 股票代码值对象 */
    private StockSymbol stockSymbol;

    /** 股票名称 */
    private String stockName;

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

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /**
     * 判断是否涨停（A股涨停为+10%，ST股+5%）
     */
    public boolean isLimitUp() {
        if (changePercent == null) {
            return false;
        }
        // ST股涨停为5%
        boolean isSt = stockName != null && stockName.contains("ST");
        BigDecimal limitThreshold = isSt
                ? new BigDecimal("4.95")
                : new BigDecimal("9.95");
        return changePercent.compareTo(limitThreshold) >= 0;
    }

    /**
     * 判断是否跌停
     */
    public boolean isLimitDown() {
        if (changePercent == null) {
            return false;
        }
        boolean isSt = stockName != null && stockName.contains("ST");
        BigDecimal limitThreshold = isSt
                ? new BigDecimal("-4.95")
                : new BigDecimal("-9.95");
        return changePercent.compareTo(limitThreshold) <= 0;
    }
}
