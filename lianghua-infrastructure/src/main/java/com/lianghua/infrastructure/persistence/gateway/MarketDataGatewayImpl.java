package com.lianghua.infrastructure.persistence.gateway;

import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.infrastructure.persistence.do_.MarketDataDO;
import com.lianghua.infrastructure.persistence.mapper.MarketDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MarketDataGatewayImpl implements MarketDataGateway {

    private final MarketDataMapper marketDataMapper;

    @Override
    public void save(MarketData marketData) {
        marketDataMapper.insert(toDataObject(marketData));
    }

    @Override
    public void saveBatch(List<MarketData> marketDataList) {
        if (marketDataList == null || marketDataList.isEmpty()) return;
        List<MarketDataDO> doList = marketDataList.stream()
                .map(this::toDataObject)
                .collect(Collectors.toList());
        marketDataMapper.insertBatch(doList);
    }

    @Override
    public Optional<MarketData> findById(String id) {
        MarketDataDO dataObject = marketDataMapper.selectById(id);
        return Optional.ofNullable(dataObject).map(this::toDomain);
    }

    @Override
    public List<MarketData> findByStockCode(String stockCode, LocalDate startDate, LocalDate endDate) {
        List<MarketDataDO> doList = marketDataMapper.selectByStockCode(stockCode, startDate, endDate);
        return doList.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<MarketData> findLatestByStockCode(String stockCode) {
        MarketDataDO dataObject = marketDataMapper.selectLatestByStockCode(stockCode);
        return Optional.ofNullable(dataObject).map(this::toDomain);
    }

    private MarketDataDO toDataObject(MarketData domain) {
        MarketDataDO dataObject = new MarketDataDO();
        dataObject.setId(domain.getId());
        dataObject.setStockCode(domain.getStockCode().getFullCode());
        dataObject.setStockName(domain.getStockName());
        dataObject.setOpenPrice(domain.getOpenPrice() != null ? domain.getOpenPrice().getValue() : null);
        dataObject.setClosePrice(domain.getClosePrice() != null ? domain.getClosePrice().getValue() : null);
        dataObject.setHighPrice(domain.getHighPrice() != null ? domain.getHighPrice().getValue() : null);
        dataObject.setLowPrice(domain.getLowPrice() != null ? domain.getLowPrice().getValue() : null);
        dataObject.setVolume(domain.getVolume());
        dataObject.setAmount(domain.getAmount());
        dataObject.setTradeDate(domain.getTradeDate());
        dataObject.setChangeRate(domain.getChangeRate());
        return dataObject;
    }

    private MarketData toDomain(MarketDataDO dataObject) {
        return MarketData.reconstruct(
                dataObject.getId(),
                dataObject.getStockCode(),
                dataObject.getStockName(),
                dataObject.getOpenPrice(),
                dataObject.getClosePrice(),
                dataObject.getHighPrice(),
                dataObject.getLowPrice(),
                dataObject.getVolume(),
                dataObject.getAmount(),
                dataObject.getTradeDate(),
                dataObject.getChangeRate()
        );
    }
}
