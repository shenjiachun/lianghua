package com.lianghua.domain.market.model.entity;

import com.lianghua.domain.market.model.vo.StockSymbol;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 市场数据领域实体
 * 充血模型：包含价格计算、涨跌判断、估值分析等领域行为
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

    // ======================== 领域行为 ========================

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

    /**
     * 计算涨跌幅（如果changePercent未设置，从当前价和昨收价推导）
     *
     * @return 涨跌幅百分比
     */
    public BigDecimal calcChangePercent() {
        if (preClosePrice == null || preClosePrice.compareTo(BigDecimal.ZERO) == 0
                || currentPrice == null) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(preClosePrice)
                .divide(preClosePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取今日振幅（%）：(最高价 - 最低价) / 昨收价 * 100
     */
    public BigDecimal getDayAmplitude() {
        if (highPrice == null || lowPrice == null || preClosePrice == null
                || preClosePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return highPrice.subtract(lowPrice)
                .divide(preClosePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 判断价格是否高于指定均线
     *
     * @param ma 均线值
     * @return 当前价格 > 均线值
     */
    public boolean isAboveMA(BigDecimal ma) {
        return ma != null && currentPrice != null && currentPrice.compareTo(ma) > 0;
    }

    /**
     * 判断是否为强势股（价格在MA5、MA10、MA20上方，即均线多头排列形态中价格居上）
     *
     * @param ma5  5日均线
     * @param ma10 10日均线
     * @param ma20 20日均线
     * @return 是否为强势状态
     */
    public boolean isStrongStock(BigDecimal ma5, BigDecimal ma10, BigDecimal ma20) {
        return isAboveMA(ma5) && isAboveMA(ma10) && isAboveMA(ma20);
    }

    /**
     * 判断估值是否偏低（PE < 20 且 PB < 2，适用于价值股）
     */
    public boolean isUndervalued() {
        boolean peOk = peRatiaTtm != null
                && peRatiaTtm.compareTo(BigDecimal.ZERO) > 0
                && peRatiaTtm.compareTo(new BigDecimal("20")) < 0;
        boolean pbOk = pbRatio != null
                && pbRatio.compareTo(BigDecimal.ZERO) > 0
                && pbRatio.compareTo(new BigDecimal("2")) < 0;
        return peOk && pbOk;
    }

    /**
     * 判断换手率是否活跃（换手率 > 3%）
     */
    public boolean isActiveTrading() {
        return turnoverRate != null && turnoverRate.compareTo(new BigDecimal("3")) > 0;
    }

    /**
     * 构建基本信息摘要字符串，用于AI提示词
     */
    public String buildBasicInfoSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("股票代码：%s，股票名称：%s，当前价格：%.2f 元",
                stockSymbol != null ? stockSymbol.getFullSymbol() : "N/A",
                stockName != null ? stockName : "N/A",
                currentPrice != null ? currentPrice : BigDecimal.ZERO));
        if (changePercent != null) {
            sb.append(String.format("，涨跌幅：%.2f%%", changePercent));
        }
        if (turnoverRate != null) {
            sb.append(String.format("，换手率：%.2f%%", turnoverRate));
        }
        if (peRatiaTtm != null) {
            sb.append(String.format("，PE(TTM)：%.2f", peRatiaTtm));
        }
        if (pbRatio != null) {
            sb.append(String.format("，PB：%.2f", pbRatio));
        }
        if (isLimitUp()) {
            sb.append("【涨停】");
        } else if (isLimitDown()) {
            sb.append("【跌停】");
        }
        return sb.toString();
    }
}
