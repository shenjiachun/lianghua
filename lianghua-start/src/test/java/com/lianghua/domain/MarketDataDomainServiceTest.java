package com.lianghua.domain;

import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
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
 * 市场数据领域模型单元测试（充血模式）
 * 测试验证业务行为直接在实体/值对象上的正确性
 */
class MarketDataDomainServiceTest {

    private final MarketDataDomainService service = new MarketDataDomainService();

    // ======================== StockSymbol 值对象测试 ========================

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
    void testValidateAndParseSymbol() {
        StockSymbol symbol = service.validateAndParseSymbol("000001.SZ");
        assertEquals("000001.SZ", symbol.getFullSymbol());
    }

    // ======================== MarketData 实体行为测试（充血模式）========================

    @Test
    void testMarketDataCalcChangePercent() {
        MarketData marketData = new MarketData();
        marketData.setCurrentPrice(new BigDecimal("11.00"));
        marketData.setPreClosePrice(new BigDecimal("10.00"));
        BigDecimal change = marketData.calcChangePercent();
        assertEquals(0, new BigDecimal("10.00").compareTo(change));
    }

    @Test
    void testMarketDataCalcChangePercentZeroBase() {
        MarketData marketData = new MarketData();
        marketData.setCurrentPrice(new BigDecimal("10.00"));
        marketData.setPreClosePrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, marketData.calcChangePercent());
    }

    @Test
    void testMarketDataIsAboveMA() {
        MarketData marketData = new MarketData();
        marketData.setCurrentPrice(new BigDecimal("15.00"));
        assertTrue(marketData.isAboveMA(new BigDecimal("14.00")));
        assertFalse(marketData.isAboveMA(new BigDecimal("16.00")));
        assertFalse(marketData.isAboveMA(null));
    }

    @Test
    void testMarketDataIsStrongStock() {
        MarketData marketData = new MarketData();
        marketData.setCurrentPrice(new BigDecimal("15.00"));

        assertTrue(marketData.isStrongStock(
                new BigDecimal("14.0"),
                new BigDecimal("13.0"),
                new BigDecimal("12.0")));

        assertFalse(marketData.isStrongStock(
                new BigDecimal("16.0"),
                new BigDecimal("13.0"),
                new BigDecimal("12.0")));
    }

    @Test
    void testMarketDataIsUndervalued() {
        MarketData marketData = new MarketData();
        marketData.setPeRatiaTtm(new BigDecimal("15.0"));
        marketData.setPbRatio(new BigDecimal("1.5"));
        assertTrue(marketData.isUndervalued());

        marketData.setPeRatiaTtm(new BigDecimal("25.0"));
        assertFalse(marketData.isUndervalued());
    }

    @Test
    void testMarketDataIsActiveTrading() {
        MarketData marketData = new MarketData();
        marketData.setTurnoverRate(new BigDecimal("5.0"));
        assertTrue(marketData.isActiveTrading());

        marketData.setTurnoverRate(new BigDecimal("1.0"));
        assertFalse(marketData.isActiveTrading());
    }

    @Test
    void testMarketDataGetDayAmplitude() {
        MarketData marketData = new MarketData();
        marketData.setHighPrice(new BigDecimal("11.00"));
        marketData.setLowPrice(new BigDecimal("9.00"));
        marketData.setPreClosePrice(new BigDecimal("10.00"));
        BigDecimal amplitude = marketData.getDayAmplitude();
        assertEquals(0, new BigDecimal("20.00").compareTo(amplitude));
    }

    @Test
    void testMarketDataBuildBasicInfoSummary() {
        MarketData marketData = new MarketData();
        marketData.setStockSymbol(StockSymbol.of("000001.SZ"));
        marketData.setStockName("平安银行");
        marketData.setCurrentPrice(new BigDecimal("12.50"));
        marketData.setChangePercent(new BigDecimal("2.50"));
        String summary = marketData.buildBasicInfoSummary();
        assertTrue(summary.contains("000001.SZ"));
        assertTrue(summary.contains("平安银行"));
        assertTrue(summary.contains("12.50"));
    }

    @Test
    void testLimitUp() {
        MarketData marketData = new MarketData();
        marketData.setStockSymbol(StockSymbol.of("000001.SZ"));
        marketData.setStockName("平安银行");
        marketData.setChangePercent(new BigDecimal("10.00"));
        assertTrue(marketData.isLimitUp());
        assertTrue(marketData.buildBasicInfoSummary().contains("涨停"));

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
        assertTrue(marketData.buildBasicInfoSummary().contains("跌停"));

        marketData.setChangePercent(new BigDecimal("-5.00"));
        assertFalse(marketData.isLimitDown());
    }

    // ======================== KLineData 实体行为测试（充血模式）========================

    @Test
    void testKLineDataIsBullish() {
        KLineData kLine = new KLineData();
        kLine.setOpenPrice(new BigDecimal("10.00"));
        kLine.setClosePrice(new BigDecimal("11.00"));
        assertTrue(kLine.isBullish());
        assertFalse(kLine.isBearish());
    }

    @Test
    void testKLineDataIsBearish() {
        KLineData kLine = new KLineData();
        kLine.setOpenPrice(new BigDecimal("11.00"));
        kLine.setClosePrice(new BigDecimal("10.00"));
        assertFalse(kLine.isBullish());
        assertTrue(kLine.isBearish());
    }

    @Test
    void testKLineDataIsDoji() {
        KLineData kLine = new KLineData();
        kLine.setOpenPrice(new BigDecimal("10.00"));
        kLine.setClosePrice(new BigDecimal("10.01")); // 0.1% difference
        assertTrue(kLine.isDoji());

        kLine.setClosePrice(new BigDecimal("10.10")); // 1% difference, not doji
        assertFalse(kLine.isDoji());
    }

    @Test
    void testKLineDataGetBodySize() {
        KLineData kLine = new KLineData();
        kLine.setOpenPrice(new BigDecimal("10.00"));
        kLine.setClosePrice(new BigDecimal("11.50"));
        assertEquals(0, new BigDecimal("1.50").compareTo(kLine.getBodySize()));
    }

    @Test
    void testKLineDataGetUpperShadow() {
        KLineData kLine = new KLineData();
        kLine.setOpenPrice(new BigDecimal("10.00"));
        kLine.setClosePrice(new BigDecimal("11.00"));
        kLine.setHighPrice(new BigDecimal("12.00"));
        kLine.setLowPrice(new BigDecimal("9.50"));
        assertEquals(0, new BigDecimal("1.00").compareTo(kLine.getUpperShadow()));
        assertEquals(0, new BigDecimal("0.50").compareTo(kLine.getLowerShadow()));
    }

    @Test
    void testKLineDataGetAmplitude() {
        KLineData kLine = new KLineData();
        kLine.setHighPrice(new BigDecimal("11.00"));
        kLine.setLowPrice(new BigDecimal("9.00"));
        kLine.setOpenPrice(new BigDecimal("10.00"));
        assertEquals(0, new BigDecimal("20.00").compareTo(kLine.getAmplitude()));
    }

    @Test
    void testKLineDataIsLimitUp() {
        KLineData kLine = new KLineData();
        kLine.setChangePercent(new BigDecimal("9.9"));
        assertTrue(kLine.isLimitUp());
        kLine.setChangePercent(new BigDecimal("5.0"));
        assertFalse(kLine.isLimitUp());
    }

    // ======================== TechnicalIndicators 静态工厂和信号测试 ========================

    @Test
    void testTechnicalIndicatorsCalcMA() {
        List<KLineData> kLines = buildKLines(new double[]{
                9.0, 9.5, 10.0, 10.5, 11.0
        });
        BigDecimal ma5 = TechnicalIndicators.calcMA(kLines, 5);
        assertNotNull(ma5);
        assertEquals(0, new BigDecimal("10.00").compareTo(ma5));
    }

    @Test
    void testTechnicalIndicatorsCalcMAInsufficientData() {
        List<KLineData> kLines = buildKLines(new double[]{9.0, 10.0});
        BigDecimal ma5 = TechnicalIndicators.calcMA(kLines, 5);
        assertNull(ma5);
    }

    @Test
    void testTechnicalIndicatorsCalcRSI() {
        List<KLineData> kLines = buildKLines(new double[]{
                10, 10.5, 11, 10.8, 11.2, 11.5, 11.3, 11.8,
                12, 11.9, 12.2, 12.5, 12.3, 12.8, 13.0
        });
        BigDecimal rsi = TechnicalIndicators.calcRSI(kLines, 14);
        assertNotNull(rsi);
        assertTrue(rsi.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(rsi.compareTo(new BigDecimal("100")) <= 0);
    }

    @Test
    void testTechnicalIndicatorsCalculate() {
        List<KLineData> kLines = buildKLines(new double[]{
                10, 10.5, 11, 10.8, 11.2, 11.5, 11.3, 11.8,
                12, 11.9, 12.2, 12.5, 12.3, 12.8, 13.0
        });
        TechnicalIndicators indicators = TechnicalIndicators.calculate(kLines);
        assertNotNull(indicators);
        assertNotNull(indicators.getMa5());
        assertNotNull(indicators.getMa10());
        assertNotNull(indicators.getRsi14());
    }

    @Test
    void testTechnicalIndicatorsIsMacdBullish() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setMacdDif(new BigDecimal("0.05"));
        indicators.setMacdDea(new BigDecimal("0.03"));
        assertTrue(indicators.isMacdBullish());
        assertFalse(indicators.isMacdBearish());
    }

    @Test
    void testTechnicalIndicatorsIsMacdBearish() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setMacdDif(new BigDecimal("-0.05"));
        indicators.setMacdDea(new BigDecimal("-0.03"));
        assertFalse(indicators.isMacdBullish());
        assertTrue(indicators.isMacdBearish());
    }

    @Test
    void testTechnicalIndicatorsRsiSignals() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setRsi14(new BigDecimal("75"));
        assertTrue(indicators.isRsiOverbought());
        assertFalse(indicators.isRsiOversold());

        indicators.setRsi14(new BigDecimal("25"));
        assertFalse(indicators.isRsiOverbought());
        assertTrue(indicators.isRsiOversold());
    }

    @Test
    void testTechnicalIndicatorsIsMaBullishAlignment() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setMa5(new BigDecimal("15"));
        indicators.setMa10(new BigDecimal("14"));
        indicators.setMa20(new BigDecimal("13"));
        indicators.setMa60(new BigDecimal("12"));
        assertTrue(indicators.isMaBullishAlignment());
        assertFalse(indicators.isMaBearishAlignment());
    }

    @Test
    void testTechnicalIndicatorsGetOverallSignal() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        // Set bullish signals
        indicators.setMa5(new BigDecimal("15"));
        indicators.setMa10(new BigDecimal("14"));
        indicators.setMa20(new BigDecimal("13"));
        indicators.setMa60(new BigDecimal("12"));
        indicators.setMacdDif(new BigDecimal("0.05"));
        indicators.setMacdDea(new BigDecimal("0.03"));
        indicators.setRsi14(new BigDecimal("55")); // neutral RSI
        assertEquals("BULLISH", indicators.getOverallSignal());
    }

    @Test
    void testTechnicalIndicatorsBollingerBreakout() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setBollingerUpper(new BigDecimal("12.00"));
        indicators.setBollingerLower(new BigDecimal("8.00"));
        assertTrue(indicators.isBollingerBreakoutUp(new BigDecimal("13.00")));
        assertFalse(indicators.isBollingerBreakoutUp(new BigDecimal("11.00")));
        assertTrue(indicators.isBollingerBreakoutDown(new BigDecimal("7.00")));
        assertFalse(indicators.isBollingerBreakoutDown(new BigDecimal("9.00")));
    }

    private List<KLineData> buildKLines(double[] prices) {
        final double OPEN_PRICE_RATIO = 0.99;
        final double HIGH_PRICE_RATIO = 1.02;
        final double LOW_PRICE_RATIO = 0.98;
        List<KLineData> kLines = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            KLineData kLine = new KLineData();
            kLine.setClosePrice(BigDecimal.valueOf(prices[i]));
            kLine.setOpenPrice(BigDecimal.valueOf(prices[i] * OPEN_PRICE_RATIO));
            kLine.setHighPrice(BigDecimal.valueOf(prices[i] * HIGH_PRICE_RATIO));
            kLine.setLowPrice(BigDecimal.valueOf(prices[i] * LOW_PRICE_RATIO));
            kLine.setTradeDate(LocalDate.now().minusDays(prices.length - i));
            kLines.add(kLine);
        }
        return kLines;
    }
}
