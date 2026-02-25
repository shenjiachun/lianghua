package com.lianghua.adapter.market;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.api.IMarketDataCmdService;
import com.lianghua.client.api.IMarketDataQueryService;
import com.lianghua.client.command.market.MarketDataSyncCmd;
import com.lianghua.client.dto.market.MarketDataDTO;
import com.lianghua.client.dto.market.MarketDataSyncResultDTO;
import com.lianghua.client.query.market.MarketDataBySymbolQuery;
import com.lianghua.client.query.market.MarketDataListQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 市场数据REST接口
 */
@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketDataController {

    private final IMarketDataQueryService marketDataQueryService;
    private final IMarketDataCmdService marketDataCmdService;

    /**
     * 根据股票代码查询实时行情
     */
    @GetMapping("/data/{symbol}")
    public SingleResponse<MarketDataDTO> getMarketData(@PathVariable String symbol) {
        MarketDataBySymbolQuery query = new MarketDataBySymbolQuery();
        query.setSymbol(symbol);
        return marketDataQueryService.getMarketDataBySymbol(query);
    }

    /**
     * 查询市场行情列表
     */
    @GetMapping("/data/list")
    public MultiResponse<MarketDataDTO> listMarketData(
            @RequestParam(required = false) String market,
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "20") int pageSize) {
        MarketDataListQuery query = new MarketDataListQuery();
        query.setMarket(market);
        query.setIndustry(industry);
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        return marketDataQueryService.listMarketData(query);
    }

    /**
     * 同步市场数据
     */
    @PostMapping("/data/sync")
    public SingleResponse<MarketDataSyncResultDTO> syncMarketData(
            @RequestBody MarketDataSyncCmd cmd) {
        return marketDataCmdService.syncMarketData(cmd);
    }
}
