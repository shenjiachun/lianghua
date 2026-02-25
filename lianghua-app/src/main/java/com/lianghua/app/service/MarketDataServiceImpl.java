package com.lianghua.app.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.app.command.FetchMarketDataCmdExe;
import com.lianghua.app.query.MarketDataQryExe;
import com.lianghua.client.api.MarketDataServiceI;
import com.lianghua.client.command.FetchMarketDataCmd;
import com.lianghua.client.dto.MarketDataDTO;
import com.lianghua.client.query.MarketDataQry;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketDataServiceImpl implements MarketDataServiceI {

    private final FetchMarketDataCmdExe fetchMarketDataCmdExe;
    private final MarketDataQryExe marketDataQryExe;
    private final MarketDataGateway marketDataGateway;

    @Override
    public Response fetchAndSaveMarketData(FetchMarketDataCmd cmd) {
        return fetchMarketDataCmdExe.execute(cmd);
    }

    @Override
    public MultiResponse<MarketDataDTO> getMarketData(MarketDataQry qry) {
        return marketDataQryExe.execute(qry);
    }

    @Override
    public SingleResponse<MarketDataDTO> getLatestMarketData(String stockCode) {
        Optional<MarketData> latest = marketDataGateway.findLatestByStockCode(stockCode);
        return latest.map(data -> SingleResponse.of(toDTO(data)))
                .orElse(SingleResponse.of(null));
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
