package com.lianghua.infrastructure.market.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * K线数据持久化对象
 */
@Data
@TableName("kline_data")
public class KLineDataDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String symbol;
    private LocalDate tradeDate;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private BigDecimal turnover;
    private BigDecimal changePercent;
    private BigDecimal turnoverRate;
    private BigDecimal adjustFactor;
    private LocalDateTime createdAt;
}
