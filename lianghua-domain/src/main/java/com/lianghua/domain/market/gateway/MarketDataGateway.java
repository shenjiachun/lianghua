package com.lianghua.domain.market.gateway;

import com.lianghua.domain.market.model.entity.MarketData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarketDataGateway {

    void save(MarketData marketData);

    void saveBatch(List<MarketData> marketDataList);

    Optional<MarketData> findById(String id);

    List<MarketData> findByStockCode(String stockCode, LocalDate startDate, LocalDate endDate);

    Optional<MarketData> findLatestByStockCode(String stockCode);
}
