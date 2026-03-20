package com.lianghua.domain;

import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.domain.market.model.vo.StockSymbol;
import com.lianghua.domain.market.service.MarketDataDomainService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 市场数据领域服务单元测试
 */
class MarketDataDomainServiceTest {

    private final MarketDataDomainService service = new MarketDataDomainService();

    @Test
    void testStockSymbolParse() {
        StockSymbol symbol = StockSymbol.of("000001.SZ");
        assertEquals("000001", symbol.getCode());
        assertEquals("SZ", symbol.getMarket());
        assertEquals("000001.SZ", symbol.getFullSymbol());
        assertTrue(symbol.isShenzhen());
        assertFalse(symbol.isShanghai());
    }

    @Test
    void testStockSymbolParseShanghai() {
        StockSymbol symbol = StockSymbol.of("600036.SH");
        assertTrue(symbol.isShanghai());
        assertFalse(symbol.isShenzhen());
    }

    @Test
    void testStockSymbolParseInvalid() {
        assertThrows(Exception.class, () -> StockSymbol.of("INVALID_FORMAT"));
        assertThrows(Exception.class, () -> StockSymbol.of(null));
        assertThrows(Exception.class, () -> StockSymbol.of(""));
    }

    @Test
    void testCalcChangePercent() {
        BigDecimal change = service.calcChangePercent(
                new BigDecimal("11.00"), new BigDecimal("10.00"));
        assertEquals(0, new BigDecimal("10.00").compareTo(change));
    }

    @Test
    void testCalcChangePercentZeroBase() {
        BigDecimal change = service.calcChangePercent(
                new BigDecimal("10.00"), BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, change);
    }

    @Test
    void testCalcMA() {
        List<KLineData> kLines = buildKLines(new double[]{
                9.0, 9.5, 10.0, 10.5, 11.0
        });
        BigDecimal ma5 = service.calcMA(kLines, 5);
        assertNotNull(ma5);
        assertEquals(0, new BigDecimal("10.00").compareTo(ma5));
    }

    @Test
    void testCalcMAInsufficientData() {
        List<KLineData> kLines = buildKLines(new double[]{9.0, 10.0});
        BigDecimal ma5 = service.calcMA(kLines, 5);
        assertNull(ma5);
    }

    @Test
    void testCalcRSI() {
        List<KLineData> kLines = buildKLines(new double[]{
                10, 10.5, 11, 10.8, 11.2, 11.5, 11.3, 11.8,
                12, 11.9, 12.2, 12.5, 12.3, 12.8, 13.0
        });
        BigDecimal rsi = service.calcRSI(kLines, 14);
        assertNotNull(rsi);
        assertTrue(rsi.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(rsi.compareTo(new BigDecimal("100")) <= 0);
    }

    @Test
    void testIsStrongStock() {
        MarketData marketData = new MarketData();
        marketData.setStockSymbol(StockSymbol.of("000001.SZ"));
        marketData.setCurrentPrice(new BigDecimal("15.00"));

        assertTrue(service.isStrongStock(marketData,
                new BigDecimal("14.0"),
                new BigDecimal("13.0"),
                new BigDecimal("12.0")));

        assertFalse(service.isStrongStock(marketData,
                new BigDecimal("16.0"),
                new BigDecimal("13.0"),
                new BigDecimal("12.0")));
    }

    @Test
    void testLimitUp() {
        MarketData marketData = new MarketData();
        marketData.setStockSymbol(StockSymbol.of("000001.SZ"));
        marketData.setStockName("平安银行");
        marketData.setChangePercent(new BigDecimal("10.00"));
        assertTrue(marketData.isLimitUp());

        marketData.setChangePercent(new BigDecimal("5.00"));
        assertFalse(marketData.isLimitUp());
    }

    @Test
    void testLimitDown() {
        MarketData marketData = new MarketData();
        marketData.setStockSymbol(StockSymbol.of("000001.SZ"));
        marketData.setStockName("平安银行");
        marketData.setChangePercent(new BigDecimal("-10.00"));
        assertTrue(marketData.isLimitDown());

        marketData.setChangePercent(new BigDecimal("-5.00"));
        assertFalse(marketData.isLimitDown());
    }

    private List<KLineData> buildKLines(double[] prices) {
        List<KLineData> kLines = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            KLineData kLine = new KLineData();
            kLine.setClosePrice(BigDecimal.valueOf(prices[i]));
            kLine.setTradeDate(LocalDate.now().minusDays(prices.length - i));
            kLines.add(kLine);
        }
        return kLines;
    }
}
