package com.lianghua.infrastructure.persistence.do_;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MarketDataDO {
    private String id;
    private String stockCode;
    private String stockName;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private Long volume;
    private BigDecimal amount;
    private LocalDate tradeDate;
    private BigDecimal changeRate;
    private LocalDateTime createTime;
}
