package com.lianghua.client.dto.analysis;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 技术指标数据传输对象
 */
@Data
public class TechnicalIndicatorsDTO {

    /** 5日均线 */
    private BigDecimal ma5;

    /** 10日均线 */
    private BigDecimal ma10;

    /** 20日均线 */
    private BigDecimal ma20;

    /** 60日均线 */
    private BigDecimal ma60;

    /** MACD（DIF） */
    private BigDecimal macdDif;

    /** MACD（DEA） */
    private BigDecimal macdDea;

    /** MACD柱 */
    private BigDecimal macdBar;

    /** RSI（14日） */
    private BigDecimal rsi14;

    /** 布林带上轨 */
    private BigDecimal bollingerUpper;

    /** 布林带中轨 */
    private BigDecimal bollingerMiddle;

    /** 布林带下轨 */
    private BigDecimal bollingerLower;

    /** KDJ-K值 */
    private BigDecimal kdjK;

    /** KDJ-D值 */
    private BigDecimal kdjD;

    /** KDJ-J值 */
    private BigDecimal kdjJ;

    /** 成交量比 */
    private BigDecimal volumeRatio;
}
