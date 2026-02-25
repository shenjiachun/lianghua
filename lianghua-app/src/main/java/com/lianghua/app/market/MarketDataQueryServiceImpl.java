package com.lianghua.app.market;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.Assert;
import com.lianghua.client.api.IMarketDataQueryService;
import com.lianghua.client.dto.market.MarketDataDTO;
import com.lianghua.client.query.market.MarketDataBySymbolQuery;
import com.lianghua.client.query.market.MarketDataListQuery;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 市场数据查询服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataQueryServiceImpl implements IMarketDataQueryService {

    private final MarketDataGateway marketDataGateway;

    @Override
    public SingleResponse<MarketDataDTO> getMarketDataBySymbol(MarketDataBySymbolQuery query) {
        Assert.notNull(query.getSymbol(), "股票代码不能为空");
        Assert.isTrue(!query.getSymbol().isBlank(), "股票代码不能为空");
        Optional<MarketData> marketDataOpt = marketDataGateway.findBySymbol(query.getSymbol());
        if (marketDataOpt.isEmpty()) {
            return SingleResponse.buildFailure("NOT_FOUND", "未找到股票数据: " + query.getSymbol());
        }
        return SingleResponse.of(toDTO(marketDataOpt.get()));
    }

    @Override
    public MultiResponse<MarketDataDTO> listMarketData(MarketDataListQuery query) {
        int pageNum = query.getPageIndex() > 0 ? query.getPageIndex() : 1;
        int pageSize = query.getPageSize() > 0 ? query.getPageSize() : 20;
        List<MarketData> list = marketDataGateway.findList(
                query.getMarket(), query.getIndustry(), pageNum, pageSize);
        List<MarketDataDTO> dtoList = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    private MarketDataDTO toDTO(MarketData entity) {
        MarketDataDTO dto = new MarketDataDTO();
        dto.setSymbol(entity.getStockSymbol() != null ? entity.getStockSymbol().getFullSymbol() : null);
        dto.setName(entity.getStockName());
        dto.setCurrentPrice(entity.getCurrentPrice());
        dto.setOpenPrice(entity.getOpenPrice());
        dto.setHighPrice(entity.getHighPrice());
        dto.setLowPrice(entity.getLowPrice());
        dto.setPreClosePrice(entity.getPreClosePrice());
        dto.setVolume(entity.getVolume());
        dto.setTurnover(entity.getTurnover());
        dto.setChangePercent(entity.getChangePercent());
        dto.setChangeAmount(entity.getChangeAmount());
        dto.setTurnoverRate(entity.getTurnoverRate());
        dto.setPeRatiaTtm(entity.getPeRatiaTtm());
        dto.setPbRatio(entity.getPbRatio());
        dto.setMarketCap(entity.getMarketCap());
        dto.setDataTime(entity.getDataTime());
        dto.setDataSource(entity.getDataSource());
        return dto;
    }
}
