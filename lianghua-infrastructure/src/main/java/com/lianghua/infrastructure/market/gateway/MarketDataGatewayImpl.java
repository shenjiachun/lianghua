package com.lianghua.infrastructure.market.gateway;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.domain.market.model.vo.StockSymbol;
import com.lianghua.infrastructure.market.dataobject.KLineDataDO;
import com.lianghua.infrastructure.market.dataobject.MarketDataDO;
import com.lianghua.infrastructure.market.mapper.KLineDataMapper;
import com.lianghua.infrastructure.market.mapper.MarketDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 市场数据网关实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataGatewayImpl implements MarketDataGateway {

    private final MarketDataMapper marketDataMapper;
    private final KLineDataMapper kLineDataMapper;

    @Override
    public Optional<MarketData> findBySymbol(String symbol) {
        LambdaQueryWrapper<MarketDataDO> wrapper = new LambdaQueryWrapper<MarketDataDO>()
                .eq(MarketDataDO::getSymbol, symbol)
                .orderByDesc(MarketDataDO::getDataTime)
                .last("LIMIT 1");
        MarketDataDO dataObject = marketDataMapper.selectOne(wrapper);
        return Optional.ofNullable(dataObject).map(this::toEntity);
    }

    @Override
    public List<MarketData> findList(String market, String industry, int pageNum, int pageSize) {
        LambdaQueryWrapper<MarketDataDO> wrapper = new LambdaQueryWrapper<MarketDataDO>()
                .orderByDesc(MarketDataDO::getDataTime)
                .last("LIMIT " + ((pageNum - 1) * pageSize) + ", " + pageSize);
        if (market != null && !market.isBlank()) {
            wrapper.like(MarketDataDO::getSymbol, "." + market.toUpperCase());
        }
        return marketDataMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdate(MarketData marketData) {
        String symbol = marketData.getStockSymbol() != null
                ? marketData.getStockSymbol().getFullSymbol() : null;
        LambdaQueryWrapper<MarketDataDO> wrapper = new LambdaQueryWrapper<MarketDataDO>()
                .eq(MarketDataDO::getSymbol, symbol);
        MarketDataDO existing = marketDataMapper.selectOne(wrapper);
        MarketDataDO dataObject = toDO(marketData);
        if (existing != null) {
            dataObject.setId(existing.getId());
            dataObject.setUpdatedAt(LocalDateTime.now());
            marketDataMapper.updateById(dataObject);
        } else {
            dataObject.setCreatedAt(LocalDateTime.now());
            dataObject.setUpdatedAt(LocalDateTime.now());
            marketDataMapper.insert(dataObject);
        }
    }

    @Override
    public MarketData fetchRealTimeData(String symbol, String dataSource) {
        // 模拟从外部数据源获取数据（实际项目中对接Tushare/EastMoney等API）
        log.info("模拟获取实时行情数据，symbol: {}, dataSource: {}", symbol, dataSource);
        MarketData mockData = new MarketData();
        mockData.setStockSymbol(StockSymbol.of(symbol));
        mockData.setStockName("模拟股票-" + symbol);
        mockData.setCurrentPrice(new BigDecimal("10.00"));
        mockData.setOpenPrice(new BigDecimal("9.80"));
        mockData.setHighPrice(new BigDecimal("10.20"));
        mockData.setLowPrice(new BigDecimal("9.70"));
        mockData.setPreClosePrice(new BigDecimal("9.90"));
        mockData.setVolume(100000L);
        mockData.setTurnover(new BigDecimal("1000000"));
        mockData.setChangePercent(new BigDecimal("1.01"));
        mockData.setChangeAmount(new BigDecimal("0.10"));
        mockData.setTurnoverRate(new BigDecimal("2.5"));
        mockData.setPeRatiaTtm(new BigDecimal("15.0"));
        mockData.setPbRatio(new BigDecimal("1.5"));
        mockData.setMarketCap(new BigDecimal("10000000000"));
        mockData.setDataTime(LocalDateTime.now());
        mockData.setDataSource(dataSource != null ? dataSource : "MOCK");
        return mockData;
    }

    @Override
    public List<KLineData> findKLineData(String symbol, int days) {
        List<KLineDataDO> dataObjects = kLineDataMapper.findRecentBySymbol(symbol, days);
        if (dataObjects.isEmpty()) {
            // 如果没有历史数据，返回模拟数据
            return generateMockKLineData(symbol, days);
        }
        return dataObjects.stream().map(this::toKLineEntity).collect(Collectors.toList());
    }

    @Override
    public void saveBatchKLineData(List<KLineData> kLineDataList) {
        if (kLineDataList == null || kLineDataList.isEmpty()) {
            return;
        }
        kLineDataList.forEach(kLine -> {
            KLineDataDO dataObject = toKLineDO(kLine);
            dataObject.setCreatedAt(LocalDateTime.now());
            kLineDataMapper.insert(dataObject);
        });
    }

    /**
     * 生成模拟K线数据（用于开发测试）
     */
    private List<KLineData> generateMockKLineData(String symbol, int days) {
        List<KLineData> mockList = new ArrayList<>();
        BigDecimal basePrice = new BigDecimal("10.00");
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = days; i >= 0; i--) {
            KLineData kLine = new KLineData();
            kLine.setSymbol(symbol);
            kLine.setTradeDate(today.minusDays(i));
            BigDecimal randomChange = BigDecimal.valueOf((Math.random() - 0.5) * 0.1);
            BigDecimal closePrice = basePrice.add(randomChange.multiply(basePrice))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            kLine.setClosePrice(closePrice);
            kLine.setOpenPrice(closePrice.subtract(new BigDecimal("0.10")));
            kLine.setHighPrice(closePrice.add(new BigDecimal("0.20")));
            kLine.setLowPrice(closePrice.subtract(new BigDecimal("0.20")));
            kLine.setVolume(50000L + (long)(Math.random() * 100000));
            kLine.setTurnoverRate(new BigDecimal("2.0"));
            mockList.add(kLine);
            basePrice = closePrice;
        }
        return mockList;
    }

    private MarketData toEntity(MarketDataDO dataObject) {
        MarketData entity = new MarketData();
        entity.setId(dataObject.getId());
        try {
            entity.setStockSymbol(StockSymbol.of(dataObject.getSymbol()));
        } catch (Exception e) {
            log.warn("解析股票代码失败: {}", dataObject.getSymbol());
        }
        entity.setStockName(dataObject.getStockName());
        entity.setCurrentPrice(dataObject.getCurrentPrice());
        entity.setOpenPrice(dataObject.getOpenPrice());
        entity.setHighPrice(dataObject.getHighPrice());
        entity.setLowPrice(dataObject.getLowPrice());
        entity.setPreClosePrice(dataObject.getPreClosePrice());
        entity.setVolume(dataObject.getVolume());
        entity.setTurnover(dataObject.getTurnover());
        entity.setChangePercent(dataObject.getChangePercent());
        entity.setChangeAmount(dataObject.getChangeAmount());
        entity.setTurnoverRate(dataObject.getTurnoverRate());
        entity.setPeRatiaTtm(dataObject.getPeRatiaTtm());
        entity.setPbRatio(dataObject.getPbRatio());
        entity.setMarketCap(dataObject.getMarketCap());
        entity.setDataTime(dataObject.getDataTime());
        entity.setDataSource(dataObject.getDataSource());
        entity.setCreatedAt(dataObject.getCreatedAt());
        entity.setUpdatedAt(dataObject.getUpdatedAt());
        return entity;
    }

    private MarketDataDO toDO(MarketData entity) {
        MarketDataDO dataObject = new MarketDataDO();
        dataObject.setId(entity.getId());
        dataObject.setSymbol(entity.getStockSymbol() != null
                ? entity.getStockSymbol().getFullSymbol() : null);
        dataObject.setStockName(entity.getStockName());
        dataObject.setCurrentPrice(entity.getCurrentPrice());
        dataObject.setOpenPrice(entity.getOpenPrice());
        dataObject.setHighPrice(entity.getHighPrice());
        dataObject.setLowPrice(entity.getLowPrice());
        dataObject.setPreClosePrice(entity.getPreClosePrice());
        dataObject.setVolume(entity.getVolume());
        dataObject.setTurnover(entity.getTurnover());
        dataObject.setChangePercent(entity.getChangePercent());
        dataObject.setChangeAmount(entity.getChangeAmount());
        dataObject.setTurnoverRate(entity.getTurnoverRate());
        dataObject.setPeRatiaTtm(entity.getPeRatiaTtm());
        dataObject.setPbRatio(entity.getPbRatio());
        dataObject.setMarketCap(entity.getMarketCap());
        dataObject.setDataTime(entity.getDataTime());
        dataObject.setDataSource(entity.getDataSource());
        return dataObject;
    }

    private KLineData toKLineEntity(KLineDataDO dataObject) {
        KLineData entity = new KLineData();
        entity.setId(dataObject.getId());
        entity.setSymbol(dataObject.getSymbol());
        entity.setTradeDate(dataObject.getTradeDate());
        entity.setOpenPrice(dataObject.getOpenPrice());
        entity.setHighPrice(dataObject.getHighPrice());
        entity.setLowPrice(dataObject.getLowPrice());
        entity.setClosePrice(dataObject.getClosePrice());
        entity.setVolume(dataObject.getVolume());
        entity.setTurnover(dataObject.getTurnover());
        entity.setChangePercent(dataObject.getChangePercent());
        entity.setTurnoverRate(dataObject.getTurnoverRate());
        entity.setAdjustFactor(dataObject.getAdjustFactor());
        return entity;
    }

    private KLineDataDO toKLineDO(KLineData entity) {
        KLineDataDO dataObject = new KLineDataDO();
        dataObject.setSymbol(entity.getSymbol());
        dataObject.setTradeDate(entity.getTradeDate());
        dataObject.setOpenPrice(entity.getOpenPrice());
        dataObject.setHighPrice(entity.getHighPrice());
        dataObject.setLowPrice(entity.getLowPrice());
        dataObject.setClosePrice(entity.getClosePrice());
        dataObject.setVolume(entity.getVolume());
        dataObject.setTurnover(entity.getTurnover());
        dataObject.setChangePercent(entity.getChangePercent());
        dataObject.setTurnoverRate(entity.getTurnoverRate());
        dataObject.setAdjustFactor(entity.getAdjustFactor());
        return dataObject;
    }
}
