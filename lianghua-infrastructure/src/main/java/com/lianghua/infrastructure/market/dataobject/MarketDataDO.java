package com.lianghua.infrastructure.market.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 市场数据持久化对象
 */
@Data
@TableName("market_data")
public class MarketDataDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String symbol;
    private String stockName;
    private BigDecimal currentPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal preClosePrice;
    private Long volume;
    private BigDecimal turnover;
    private BigDecimal changePercent;
    private BigDecimal changeAmount;
    private BigDecimal turnoverRate;
    private BigDecimal peRatiaTtm;
    private BigDecimal pbRatio;
    private BigDecimal marketCap;
    private LocalDateTime dataTime;
    private String dataSource;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
