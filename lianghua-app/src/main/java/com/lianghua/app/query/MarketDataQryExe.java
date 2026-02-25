package com.lianghua.app.query;

import com.alibaba.cola.dto.MultiResponse;
import com.lianghua.client.dto.MarketDataDTO;
import com.lianghua.client.query.MarketDataQry;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MarketDataQryExe {

    private final MarketDataGateway marketDataGateway;

    public MultiResponse<MarketDataDTO> execute(MarketDataQry qry) {
        List<MarketData> dataList = marketDataGateway.findByStockCode(
                qry.getStockCode(), qry.getStartDate(), qry.getEndDate());
        List<MarketDataDTO> dtos = dataList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
    }

    private MarketDataDTO toDTO(MarketData data) {
        MarketDataDTO dto = new MarketDataDTO();
        dto.setStockCode(data.getStockCode().getFullCode());
        dto.setStockName(data.getStockName());
        dto.setOpenPrice(data.getOpenPrice() != null ? data.getOpenPrice().getValue() : null);
        dto.setClosePrice(data.getClosePrice() != null ? data.getClosePrice().getValue() : null);
        dto.setHighPrice(data.getHighPrice() != null ? data.getHighPrice().getValue() : null);
        dto.setLowPrice(data.getLowPrice() != null ? data.getLowPrice().getValue() : null);
        dto.setVolume(data.getVolume());
        dto.setAmount(data.getAmount());
        dto.setTradeDate(data.getTradeDate());
        dto.setChangeRate(data.getChangeRate());
        return dto;
    }
}
