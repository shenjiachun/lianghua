package com.lianghua.domain.market.service;

import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.domain.market.model.vo.StockSymbol;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 市场数据领域服务
 */
@Service
public class MarketDataDomainService {

    /**
     * 验证股票代码合法性
     */
    public StockSymbol validateAndParseSymbol(String symbolStr) {
        return StockSymbol.of(symbolStr);
    }

    /**
     * 计算涨跌幅
     */
    public BigDecimal calcChangePercent(BigDecimal currentPrice, BigDecimal preClosePrice) {
        if (preClosePrice == null || preClosePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentPrice.subtract(preClosePrice)
                .divide(preClosePrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算简单移动平均线
     *
     * @param kLines K线列表（按日期升序）
     * @param period 周期
     * @return 最新MA值
     */
    public BigDecimal calcMA(List<KLineData> kLines, int period) {
        if (kLines == null || kLines.size() < period) {
            return null;
        }
        List<KLineData> recent = kLines.subList(kLines.size() - period, kLines.size());
        BigDecimal sum = recent.stream()
                .map(KLineData::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(period), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算RSI（14日）
     *
     * @param kLines K线列表（按日期升序，至少需要15条）
     * @return RSI值（0-100）
     */
    public BigDecimal calcRSI(List<KLineData> kLines, int period) {
        if (kLines == null || kLines.size() < period + 1) {
            return null;
        }
        BigDecimal gainSum = BigDecimal.ZERO;
        BigDecimal lossSum = BigDecimal.ZERO;
        int start = kLines.size() - period;
        for (int i = start; i < kLines.size(); i++) {
            BigDecimal change = kLines.get(i).getClosePrice()
                    .subtract(kLines.get(i - 1).getClosePrice());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gainSum = gainSum.add(change);
            } else {
                lossSum = lossSum.add(change.abs());
            }
        }
        BigDecimal avgGain = gainSum.divide(new BigDecimal(period), 4, RoundingMode.HALF_UP);
        BigDecimal avgLoss = lossSum.divide(new BigDecimal(period), 4, RoundingMode.HALF_UP);
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal("100");
        }
        BigDecimal rs = avgGain.divide(avgLoss, 4, RoundingMode.HALF_UP);
        BigDecimal rsi = new BigDecimal("100")
                .subtract(new BigDecimal("100").divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP));
        return rsi;
    }

    /**
     * 判断是否是强势股（价格在5日、10日、20日均线上方）
     */
    public boolean isStrongStock(MarketData marketData, BigDecimal ma5, BigDecimal ma10, BigDecimal ma20) {
        if (marketData == null || marketData.getCurrentPrice() == null) {
            return false;
        }
        BigDecimal price = marketData.getCurrentPrice();
        return (ma5 == null || price.compareTo(ma5) > 0)
                && (ma10 == null || price.compareTo(ma10) > 0)
                && (ma20 == null || price.compareTo(ma20) > 0);
    }
}
