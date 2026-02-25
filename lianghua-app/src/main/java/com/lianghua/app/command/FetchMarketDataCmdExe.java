package com.lianghua.app.command;

import com.alibaba.cola.dto.Response;
import com.lianghua.client.command.FetchMarketDataCmd;
import com.lianghua.domain.market.gateway.MarketDataFetcherGateway;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchMarketDataCmdExe {

    private final MarketDataFetcherGateway fetcherGateway;
    private final MarketDataGateway marketDataGateway;

    public Response execute(FetchMarketDataCmd cmd) {
        List<MarketData> dataList = fetcherGateway.fetchMarketData(
                cmd.getStockCode(), cmd.getStartDate(), cmd.getEndDate());
        marketDataGateway.saveBatch(dataList);
        return Response.buildSuccess();
    }
}
