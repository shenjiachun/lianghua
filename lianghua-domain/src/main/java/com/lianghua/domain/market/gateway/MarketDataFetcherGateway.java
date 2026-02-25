package com.lianghua.domain.market.gateway;

import com.lianghua.domain.market.model.entity.MarketData;

import java.time.LocalDate;
import java.util.List;

public interface MarketDataFetcherGateway {

    List<MarketData> fetchMarketData(String stockCode, LocalDate startDate, LocalDate endDate);
}
