package com.lianghua.domain.market.gateway;

import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;

import java.util.List;
import java.util.Optional;

/**
 * 市场数据网关接口（领域层定义，基础设施层实现）
 */
public interface MarketDataGateway {

    /**
     * 根据股票代码查询最新市场数据
     */
    Optional<MarketData> findBySymbol(String symbol);

    /**
     * 查询市场数据列表
     */
    List<MarketData> findList(String market, String industry, int pageNum, int pageSize);

    /**
     * 保存或更新市场数据
     */
    void saveOrUpdate(MarketData marketData);

    /**
     * 从外部数据源获取实时行情
     */
    MarketData fetchRealTimeData(String symbol, String dataSource);

    /**
     * 查询历史K线数据
     */
    List<KLineData> findKLineData(String symbol, int days);

    /**
     * 批量保存K线数据
     */
    void saveBatchKLineData(List<KLineData> kLineDataList);
}
