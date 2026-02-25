package com.lianghua.client.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MarketDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stockCode;
    private String stockName;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Long volume;
    private BigDecimal amount;
    private LocalDate tradeDate;
    /** 涨跌幅 */
    private BigDecimal changeRate;
}
