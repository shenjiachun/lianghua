package com.lianghua.domain.analysis.model.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 技术指标值对象（嵌入AnalysisReport中）
 */
@Data
public class TechnicalIndicators {

    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma60;
    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private BigDecimal rsi14;
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerMiddle;
    private BigDecimal bollingerLower;
    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;
    private BigDecimal volumeRatio;
}
