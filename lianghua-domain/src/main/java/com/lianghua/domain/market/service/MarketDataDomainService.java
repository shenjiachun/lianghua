package com.lianghua.domain.market.service;

import com.lianghua.domain.market.model.vo.StockSymbol;
import org.springframework.stereotype.Service;

/**
 * 市场数据领域服务
 * 充血模型重构后，仅保留跨实体/跨聚合的领域逻辑。
 * 单实体的业务行为已移至对应实体（MarketData、KLineData）中。
 * 技术指标计算（MA、RSI、MACD等）已移至 TechnicalIndicators.calculate() 静态工厂方法。
 */
@Service
public class MarketDataDomainService {

    /**
     * 验证并解析股票代码
     * 此逻辑属于跨领域入口校验，保留在领域服务中。
     *
     * @param symbolStr 股票代码字符串，如 "000001.SZ"
     * @return 股票代码值对象
     */
    public StockSymbol validateAndParseSymbol(String symbolStr) {
        return StockSymbol.of(symbolStr);
    }
}
