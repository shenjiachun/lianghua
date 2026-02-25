package com.lianghua.domain.market.service;

import com.lianghua.domain.market.model.entity.MarketData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MarketDataDomainService {

    /**
     * Calculates Simple Moving Average for the given period.
     */
    public List<BigDecimal> calculateMovingAverage(List<MarketData> dataList, int period) {
        List<BigDecimal> result = new ArrayList<>();
        if (dataList == null || dataList.size() < period) {
            return result;
        }
        for (int i = period - 1; i < dataList.size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i - period + 1; j <= i; j++) {
                sum = sum.add(dataList.get(j).getClosePrice().getValue());
            }
            result.add(sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP));
        }
        return result;
    }

    /**
     * Calculates RSI (Relative Strength Index) for the given period.
     */
    public BigDecimal calculateRSI(List<MarketData> dataList, int period) {
        if (dataList == null || dataList.size() <= period) {
            return BigDecimal.valueOf(50);
        }
        BigDecimal gainSum = BigDecimal.ZERO;
        BigDecimal lossSum = BigDecimal.ZERO;

        for (int i = 1; i <= period; i++) {
            BigDecimal change = dataList.get(i).getClosePrice().getValue()
                    .subtract(dataList.get(i - 1).getClosePrice().getValue());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gainSum = gainSum.add(change);
            } else {
                lossSum = lossSum.add(change.abs());
            }
        }

        BigDecimal avgGain = gainSum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
        BigDecimal avgLoss = lossSum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, 8, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100)
                .subtract(BigDecimal.valueOf(100)
                        .divide(BigDecimal.ONE.add(rs), 4, RoundingMode.HALF_UP));
        return rsi;
    }

    /**
     * Calculates MACD (12, 26, 9) signal.
     * Returns a map with keys: macd, signal, histogram
     */
    public Map<String, BigDecimal> calculateMACD(List<MarketData> dataList) {
        Map<String, BigDecimal> result = new HashMap<>();
        if (dataList == null || dataList.size() < 26) {
            result.put("macd", BigDecimal.ZERO);
            result.put("signal", BigDecimal.ZERO);
            result.put("histogram", BigDecimal.ZERO);
            return result;
        }

        List<BigDecimal> closes = dataList.stream()
                .map(d -> d.getClosePrice().getValue())
                .collect(Collectors.toList());

        BigDecimal ema12 = calculateEMA(closes, 12);
        BigDecimal ema26 = calculateEMA(closes, 26);
        BigDecimal macdLine = ema12.subtract(ema26);

        // Signal line is 9-day EMA of MACD - simplified to use same last value
        BigDecimal signalLine = macdLine.multiply(BigDecimal.valueOf(0.2))
                .add(macdLine.multiply(BigDecimal.valueOf(0.8)));
        BigDecimal histogram = macdLine.subtract(signalLine);

        result.put("macd", macdLine.setScale(4, RoundingMode.HALF_UP));
        result.put("signal", signalLine.setScale(4, RoundingMode.HALF_UP));
        result.put("histogram", histogram.setScale(4, RoundingMode.HALF_UP));
        return result;
    }

    private BigDecimal calculateEMA(List<BigDecimal> prices, int period) {
        if (prices.isEmpty()) return BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = prices.get(0);
        for (int i = 1; i < prices.size(); i++) {
            ema = prices.get(i).multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
        }
        return ema;
    }

    /**
     * Generates a text summary of market data for AI analysis.
     */
    public String generateMarketSummary(List<MarketData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return "No market data available for analysis.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("股票代码: ").append(dataList.get(0).getStockCode().getFullCode()).append("\n");
        sb.append("分析周期: ").append(dataList.size()).append(" 个交易日\n\n");

        // Latest data
        MarketData latest = dataList.get(dataList.size() - 1);
        sb.append("最新交易日 (").append(latest.getTradeDate()).append("):\n");
        sb.append("  开盘价: ").append(latest.getOpenPrice().getValue()).append("\n");
        sb.append("  收盘价: ").append(latest.getClosePrice().getValue()).append("\n");
        sb.append("  最高价: ").append(latest.getHighPrice().getValue()).append("\n");
        sb.append("  最低价: ").append(latest.getLowPrice().getValue()).append("\n");
        sb.append("  成交量: ").append(latest.getVolume()).append("\n");
        sb.append("  涨跌幅: ").append(latest.getChangeRate()).append("%\n\n");

        // Technical indicators
        if (dataList.size() >= 5) {
            List<BigDecimal> ma5 = calculateMovingAverage(dataList, 5);
            if (!ma5.isEmpty()) {
                sb.append("MA5: ").append(ma5.get(ma5.size() - 1)).append("\n");
            }
        }
        if (dataList.size() >= 10) {
            List<BigDecimal> ma10 = calculateMovingAverage(dataList, 10);
            if (!ma10.isEmpty()) {
                sb.append("MA10: ").append(ma10.get(ma10.size() - 1)).append("\n");
            }
        }
        if (dataList.size() >= 20) {
            List<BigDecimal> ma20 = calculateMovingAverage(dataList, 20);
            if (!ma20.isEmpty()) {
                sb.append("MA20: ").append(ma20.get(ma20.size() - 1)).append("\n");
            }
        }
        if (dataList.size() >= 15) {
            BigDecimal rsi = calculateRSI(dataList, 14);
            sb.append("RSI(14): ").append(rsi).append("\n");
        }

        // Price trend (last 5 days)
        int lookback = Math.min(5, dataList.size());
        sb.append("\n近").append(lookback).append("日走势:\n");
        for (int i = dataList.size() - lookback; i < dataList.size(); i++) {
            MarketData d = dataList.get(i);
            sb.append("  ").append(d.getTradeDate())
                    .append(" 收盘:").append(d.getClosePrice().getValue())
                    .append(" 涨跌:").append(d.getChangeRate()).append("%\n");
        }

        // Bull/bear count
        long bullDays = dataList.stream().filter(MarketData::isBullish).count();
        long bearDays = dataList.stream().filter(MarketData::isBearish).count();
        sb.append("\n上涨天数: ").append(bullDays).append(", 下跌天数: ").append(bearDays).append("\n");

        return sb.toString();
    }
}
