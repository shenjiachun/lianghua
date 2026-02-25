package com.lianghua.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.command.FetchMarketDataCmd;
import com.lianghua.client.dto.MarketDataDTO;
import com.lianghua.client.query.MarketDataQry;

public interface MarketDataServiceI {

    MultiResponse<MarketDataDTO> getMarketData(MarketDataQry qry);

    SingleResponse<MarketDataDTO> getLatestMarketData(String stockCode);

    Response fetchAndSaveMarketData(FetchMarketDataCmd cmd);
}
