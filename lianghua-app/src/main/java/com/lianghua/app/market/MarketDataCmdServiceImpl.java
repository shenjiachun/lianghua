package com.lianghua.app.market;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.Assert;
import com.lianghua.client.api.IMarketDataCmdService;
import com.lianghua.client.command.market.MarketDataSyncCmd;
import com.lianghua.client.dto.market.MarketDataSyncResultDTO;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 市场数据命令服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataCmdServiceImpl implements IMarketDataCmdService {

    private final MarketDataGateway marketDataGateway;

    @Override
    public SingleResponse<MarketDataSyncResultDTO> syncMarketData(MarketDataSyncCmd cmd) {
        Assert.notNull(cmd.getSymbol(), "股票代码不能为空");
        Assert.isTrue(!cmd.getSymbol().isBlank(), "股票代码不能为空");
        log.info("开始同步市场数据，股票代码：{}，数据来源：{}", cmd.getSymbol(), cmd.getDataSource());
        try {
            MarketData marketData = marketDataGateway.fetchRealTimeData(
                    cmd.getSymbol(), cmd.getDataSource());
            marketDataGateway.saveOrUpdate(marketData);
            MarketDataSyncResultDTO result = new MarketDataSyncResultDTO();
            result.setSymbol(cmd.getSymbol());
            result.setSuccess(true);
            result.setSyncCount(1);
            result.setMessage("数据同步成功");
            log.info("市场数据同步完成，股票代码：{}", cmd.getSymbol());
            return SingleResponse.of(result);
        } catch (Exception e) {
            log.error("市场数据同步失败，股票代码：{}", cmd.getSymbol(), e);
            MarketDataSyncResultDTO result = new MarketDataSyncResultDTO();
            result.setSymbol(cmd.getSymbol());
            result.setSuccess(false);
            result.setSyncCount(0);
            result.setMessage("数据同步失败：" + e.getMessage());
            return SingleResponse.buildFailure("SYNC_FAILED", e.getMessage());
        }
    }
}
