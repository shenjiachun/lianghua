package com.lianghua.client.api;

import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.command.market.MarketDataSyncCmd;
import com.lianghua.client.dto.market.MarketDataSyncResultDTO;

/**
 * 市场数据命令服务接口
 */
public interface IMarketDataCmdService {

    /**
     * 同步市场数据
     */
    SingleResponse<MarketDataSyncResultDTO> syncMarketData(MarketDataSyncCmd cmd);
}
