package com.lianghua.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.dto.market.MarketDataDTO;
import com.lianghua.client.query.market.MarketDataBySymbolQuery;
import com.lianghua.client.query.market.MarketDataListQuery;

/**
 * 市场数据查询服务接口
 */
public interface IMarketDataQueryService {

    /**
     * 根据股票代码查询最新市场数据
     */
    SingleResponse<MarketDataDTO> getMarketDataBySymbol(MarketDataBySymbolQuery query);

    /**
     * 查询市场数据列表（支持分页）
     */
    MultiResponse<MarketDataDTO> listMarketData(MarketDataListQuery query);
}
