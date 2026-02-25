package com.lianghua.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.api.MarketDataServiceI;
import com.lianghua.client.command.FetchMarketDataCmd;
import com.lianghua.client.dto.MarketDataDTO;
import com.lianghua.client.query.MarketDataQry;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketDataController {

    private final MarketDataServiceI marketDataService;

    @PostMapping("/fetch")
    public Response fetchMarketData(@RequestBody FetchMarketDataCmd cmd) {
        return marketDataService.fetchAndSaveMarketData(cmd);
    }

    @GetMapping("/{stockCode}")
    public MultiResponse<MarketDataDTO> getMarketData(
            @PathVariable String stockCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        MarketDataQry qry = new MarketDataQry();
        qry.setStockCode(stockCode);
        qry.setStartDate(startDate);
        qry.setEndDate(endDate);
        return marketDataService.getMarketData(qry);
    }

    @GetMapping("/{stockCode}/latest")
    public SingleResponse<MarketDataDTO> getLatestMarketData(@PathVariable String stockCode) {
        return marketDataService.getLatestMarketData(stockCode);
    }
}
