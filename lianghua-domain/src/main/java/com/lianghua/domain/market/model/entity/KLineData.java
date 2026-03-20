package com.lianghua.domain.market.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * K线数据实体（日K线）
 * 充血模型：包含K线形态判断、价格计算等领域行为
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

    // ======================== 领域行为 ========================

    /**
     * 判断是否为阳线（收盘价 > 开盘价）
     */
    public boolean isBullish() {
        return closePrice != null && openPrice != null
                && closePrice.compareTo(openPrice) > 0;
    }

    /**
     * 判断是否为阴线（收盘价 < 开盘价）
     */
    public boolean isBearish() {
        return closePrice != null && openPrice != null
                && closePrice.compareTo(openPrice) < 0;
    }

    /**
     * 判断是否为十字星（开盘价 ≈ 收盘价，差值在0.3%以内）
     */
    public boolean isDoji() {
        if (closePrice == null || openPrice == null || openPrice.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        BigDecimal bodyRatio = closePrice.subtract(openPrice).abs()
                .divide(openPrice, 4, RoundingMode.HALF_UP);
        return bodyRatio.compareTo(new BigDecimal("0.003")) <= 0;
    }

    /**
     * 获取实体大小（|收盘价 - 开盘价|）
     */
    public BigDecimal getBodySize() {
        if (closePrice == null || openPrice == null) {
            return BigDecimal.ZERO;
        }
        return closePrice.subtract(openPrice).abs().setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取上影线长度（最高价 - max(开盘价, 收盘价)）
     */
    public BigDecimal getUpperShadow() {
        if (highPrice == null || openPrice == null || closePrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal top = openPrice.max(closePrice);
        return highPrice.subtract(top).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取下影线长度（min(开盘价, 收盘价) - 最低价）
     */
    public BigDecimal getLowerShadow() {
        if (lowPrice == null || openPrice == null || closePrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal bottom = openPrice.min(closePrice);
        return bottom.subtract(lowPrice).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取振幅（%）：(最高价 - 最低价) / 开盘价 * 100
     */
    public BigDecimal getAmplitude() {
        if (highPrice == null || lowPrice == null || openPrice == null
                || openPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return highPrice.subtract(lowPrice)
                .divide(openPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 判断是否为锤子线（下影线长度 >= 实体大小的2倍，上影线很短）
     * 锤子线通常出现在下跌趋势末端，预示反转
     */
    public boolean isHammer() {
        BigDecimal body = getBodySize();
        BigDecimal lower = getLowerShadow();
        BigDecimal upper = getUpperShadow();
        if (body.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return lower.compareTo(body.multiply(new BigDecimal("2"))) >= 0
                && upper.compareTo(body.multiply(new BigDecimal("0.3"))) <= 0;
    }

    /**
     * 判断是否为射击之星（上影线长度 >= 实体大小的2倍，下影线很短）
     * 射击之星通常出现在上涨趋势末端，预示反转
     */
    public boolean isShootingStar() {
        BigDecimal body = getBodySize();
        BigDecimal lower = getLowerShadow();
        BigDecimal upper = getUpperShadow();
        if (body.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return upper.compareTo(body.multiply(new BigDecimal("2"))) >= 0
                && lower.compareTo(body.multiply(new BigDecimal("0.3"))) <= 0;
    }

    /**
     * 判断是否涨停（涨跌幅 >= 9.9%）
     */
    public boolean isLimitUp() {
        return changePercent != null && changePercent.compareTo(new BigDecimal("9.9")) >= 0;
    }

    /**
     * 判断是否跌停（涨跌幅 <= -9.9%）
     */
    public boolean isLimitDown() {
        return changePercent != null && changePercent.compareTo(new BigDecimal("-9.9")) <= 0;
    }
}
