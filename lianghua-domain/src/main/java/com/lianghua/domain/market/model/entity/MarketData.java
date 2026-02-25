package com.lianghua.domain.market.model.entity;

import com.lianghua.domain.market.model.valobj.Price;
import com.lianghua.domain.market.model.valobj.StockCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class MarketData {
    private String id;
    private StockCode stockCode;
    private String stockName;
    private Price openPrice;
    private Price closePrice;
    private Price highPrice;
    private Price lowPrice;
    private Long volume;
    private BigDecimal amount;
    private LocalDate tradeDate;
    private BigDecimal changeRate;

    private MarketData() {}

    public static MarketData create(String stockCode, String stockName,
                                    BigDecimal open, BigDecimal close,
                                    BigDecimal high, BigDecimal low,
                                    Long volume, BigDecimal amount,
                                    LocalDate tradeDate, BigDecimal changeRate) {
        MarketData md = new MarketData();
        md.id = UUID.randomUUID().toString();
        md.stockCode = StockCode.of(stockCode);
        md.stockName = stockName;
        md.openPrice = Price.of(open);
        md.closePrice = Price.of(close);
        md.highPrice = Price.of(high);
        md.lowPrice = Price.of(low);
        md.volume = volume;
        md.amount = amount;
        md.tradeDate = tradeDate;
        md.changeRate = changeRate;
        return md;
    }

    /** Reconstruct from persistence with existing id */
    public static MarketData reconstruct(String id, String stockCode, String stockName,
                                         BigDecimal open, BigDecimal close,
                                         BigDecimal high, BigDecimal low,
                                         Long volume, BigDecimal amount,
                                         LocalDate tradeDate, BigDecimal changeRate) {
        MarketData md = new MarketData();
        md.id = id;
        md.stockCode = StockCode.of(stockCode);
        md.stockName = stockName;
        md.openPrice = Price.of(open);
        md.closePrice = Price.of(close);
        md.highPrice = Price.of(high);
        md.lowPrice = Price.of(low);
        md.volume = volume;
        md.amount = amount;
        md.tradeDate = tradeDate;
        md.changeRate = changeRate;
        return md;
    }

    public boolean isBullish() {
        return changeRate != null && changeRate.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isBearish() {
        return changeRate != null && changeRate.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getPriceRange() {
        if (highPrice == null || lowPrice == null) {
            return BigDecimal.ZERO;
        }
        return highPrice.getValue().subtract(lowPrice.getValue());
    }
}
