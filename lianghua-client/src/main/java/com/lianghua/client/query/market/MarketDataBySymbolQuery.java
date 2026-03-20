package com.lianghua.client.query.market;

import com.alibaba.cola.dto.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据股票代码查询市场数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketDataBySymbolQuery extends Query {

    /** 股票代码，如 000001.SZ */
    private String symbol;
}
