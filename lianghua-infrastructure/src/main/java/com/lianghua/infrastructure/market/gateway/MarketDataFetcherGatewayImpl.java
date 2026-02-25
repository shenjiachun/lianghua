package com.lianghua.infrastructure.market.gateway;

import com.lianghua.domain.market.gateway.MarketDataFetcherGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MarketDataFetcherGatewayImpl implements MarketDataFetcherGateway {

    private static final Logger log = LoggerFactory.getLogger(MarketDataFetcherGatewayImpl.class);

    @Override
    public List<MarketData> fetchMarketData(String stockCode, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching market data for {} from {} to {}", stockCode, startDate, endDate);
        // Return mock data so the system works without external APIs
        return generateMockData(stockCode, startDate, endDate);
    }

    /**
     * Generates realistic mock market data for demonstration purposes.
     */
    private List<MarketData> generateMockData(String stockCode, LocalDate startDate, LocalDate endDate) {
        List<MarketData> result = new ArrayList<>();
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        Random random = new Random(stockCode.hashCode());
        // Base price around 10-50 CNY
        double basePrice = 10 + (Math.abs(stockCode.hashCode()) % 40);
        BigDecimal currentPrice = BigDecimal.valueOf(basePrice).setScale(2, RoundingMode.HALF_UP);

        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            // Skip weekends
            if (date.getDayOfWeek().getValue() <= 5) {
                // Random daily change between -5% and +5%
                double changePercent = (random.nextDouble() - 0.5) * 10;
                BigDecimal changeRate = BigDecimal.valueOf(changePercent).setScale(2, RoundingMode.HALF_UP);

                BigDecimal openPrice = currentPrice;
                BigDecimal closePrice = currentPrice.multiply(
                        BigDecimal.ONE.add(changeRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP))
                ).setScale(2, RoundingMode.HALF_UP);

                // High and low are within 2% of open/close range
                BigDecimal rangeHigh = openPrice.max(closePrice);
                BigDecimal rangeLow = openPrice.min(closePrice);
                BigDecimal highPrice = rangeHigh.multiply(
                        BigDecimal.ONE.add(BigDecimal.valueOf(random.nextDouble() * 0.02))
                ).setScale(2, RoundingMode.HALF_UP);
                BigDecimal lowPrice = rangeLow.multiply(
                        BigDecimal.ONE.subtract(BigDecimal.valueOf(random.nextDouble() * 0.02))
                ).setScale(2, RoundingMode.HALF_UP);

                long volume = (long) (1000000 + random.nextInt(9000000));
                BigDecimal amount = closePrice.multiply(BigDecimal.valueOf(volume))
                        .setScale(2, RoundingMode.HALF_UP);

                String stockName = getStockName(stockCode);

                MarketData marketData = MarketData.create(
                        stockCode, stockName,
                        openPrice, closePrice, highPrice, lowPrice,
                        volume, amount, date, changeRate
                );
                result.add(marketData);

                currentPrice = closePrice;
            }
            date = date.plusDays(1);
        }

        log.info("Generated {} mock market data records for {}", result.size(), stockCode);
        return result;
    }

    private String getStockName(String stockCode) {
        // Common A-share stock names for demo
        return switch (stockCode) {
            case "000001.SZ" -> "平安银行";
            case "600000.SH" -> "浦发银行";
            case "600036.SH" -> "招商银行";
            case "000651.SZ" -> "格力电器";
            case "000858.SZ" -> "五粮液";
            case "600519.SH" -> "贵州茅台";
            case "601318.SH" -> "中国平安";
            case "000002.SZ" -> "万科A";
            default -> "股票" + stockCode;
        };
    }
}
