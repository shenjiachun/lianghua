package com.lianghua.domain.analysis.model.entity;

import com.lianghua.domain.market.model.entity.KLineData;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 技术指标值对象（嵌入AnalysisReport中）
 * 充血模型：包含技术指标计算和信号判断等领域行为
 */
@Data
public class TechnicalIndicators {

    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;
    private BigDecimal ma60;
    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private BigDecimal rsi14;
    private BigDecimal bollingerUpper;
    private BigDecimal bollingerMiddle;
    private BigDecimal bollingerLower;
    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;
    private BigDecimal volumeRatio;

    // ======================== 静态工厂方法 ========================

    /**
     * 根据K线列表计算所有技术指标
     *
     * @param kLines K线列表（按日期升序）
     * @return 计算完成的技术指标对象
     */
    public static TechnicalIndicators calculate(List<KLineData> kLines) {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.ma5 = calcMA(kLines, 5);
        indicators.ma10 = calcMA(kLines, 10);
        indicators.ma20 = calcMA(kLines, 20);
        indicators.ma60 = calcMA(kLines, 60);
        indicators.rsi14 = calcRSI(kLines, 14);
        indicators.calcBollinger(kLines);
        indicators.calcMACD(kLines);
        return indicators;
    }

    // ======================== 领域行为：信号判断 ========================

    /**
     * 判断MACD是否呈多头信号（DIF > 0 且 DIF > DEA，即金叉区域）
     */
    public boolean isMacdBullish() {
        return macdDif != null && macdDea != null
                && macdDif.compareTo(BigDecimal.ZERO) > 0
                && macdDif.compareTo(macdDea) > 0;
    }

    /**
     * 判断MACD是否呈空头信号（DIF < 0 且 DIF < DEA，即死叉区域）
     */
    public boolean isMacdBearish() {
        return macdDif != null && macdDea != null
                && macdDif.compareTo(BigDecimal.ZERO) < 0
                && macdDif.compareTo(macdDea) < 0;
    }

    /**
     * 判断RSI是否超买（RSI > 70）
     */
    public boolean isRsiOverbought() {
        return rsi14 != null && rsi14.compareTo(new BigDecimal("70")) > 0;
    }

    /**
     * 判断RSI是否超卖（RSI < 30）
     */
    public boolean isRsiOversold() {
        return rsi14 != null && rsi14.compareTo(new BigDecimal("30")) < 0;
    }

    /**
     * 判断均线是否呈多头排列（MA5 > MA10 > MA20 > MA60）
     */
    public boolean isMaBullishAlignment() {
        if (ma5 == null || ma10 == null || ma20 == null) {
            return false;
        }
        boolean result = ma5.compareTo(ma10) > 0 && ma10.compareTo(ma20) > 0;
        if (ma60 != null) {
            result = result && ma20.compareTo(ma60) > 0;
        }
        return result;
    }

    /**
     * 判断均线是否呈空头排列（MA5 < MA10 < MA20 < MA60）
     */
    public boolean isMaBearishAlignment() {
        if (ma5 == null || ma10 == null || ma20 == null) {
            return false;
        }
        boolean result = ma5.compareTo(ma10) < 0 && ma10.compareTo(ma20) < 0;
        if (ma60 != null) {
            result = result && ma20.compareTo(ma60) < 0;
        }
        return result;
    }

    /**
     * 判断价格是否突破布林带上轨（传入当前价格）
     *
     * @param currentPrice 当前价格
     * @return 是否突破上轨
     */
    public boolean isBollingerBreakoutUp(BigDecimal currentPrice) {
        return bollingerUpper != null && currentPrice != null
                && currentPrice.compareTo(bollingerUpper) > 0;
    }

    /**
     * 判断价格是否跌破布林带下轨（传入当前价格）
     *
     * @param currentPrice 当前价格
     * @return 是否跌破下轨
     */
    public boolean isBollingerBreakoutDown(BigDecimal currentPrice) {
        return bollingerLower != null && currentPrice != null
                && currentPrice.compareTo(bollingerLower) < 0;
    }

    /**
     * 综合判断市场信号强度
     * 正值越大表示多头信号越强，负值越大（绝对值）表示空头信号越强
     *
     * @return 信号分数（-3 到 +3）
     */
    public int getOverallSignalScore() {
        int score = 0;
        if (isMacdBullish()) score++;
        if (isMacdBearish()) score--;
        if (isMaBullishAlignment()) score++;
        if (isMaBearishAlignment()) score--;
        if (isRsiOversold()) score++;
        if (isRsiOverbought()) score--;
        return score;
    }

    /**
     * 获取综合信号描述
     *
     * @return "BULLISH"（多头）、"BEARISH"（空头）或 "NEUTRAL"（中性）
     */
    public String getOverallSignal() {
        int score = getOverallSignalScore();
        if (score > 0) return "BULLISH";
        if (score < 0) return "BEARISH";
        return "NEUTRAL";
    }

    // ======================== 私有计算方法 ========================

    private void calcBollinger(List<KLineData> kLines) {
        int period = 20;
        if (kLines == null || kLines.size() < period || ma20 == null) {
            return;
        }
        List<KLineData> recent = kLines.subList(kLines.size() - period, kLines.size());
        BigDecimal variance = recent.stream()
                .map(k -> k.getClosePrice().subtract(ma20).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(period), 4, RoundingMode.HALF_UP);
        BigDecimal stdDev = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal twoStdDev = stdDev.multiply(new BigDecimal("2"));
        bollingerMiddle = ma20;
        bollingerUpper = ma20.add(twoStdDev).setScale(2, RoundingMode.HALF_UP);
        bollingerLower = ma20.subtract(twoStdDev).setScale(2, RoundingMode.HALF_UP);
    }

    private void calcMACD(List<KLineData> kLines) {
        if (kLines == null || kLines.size() < 26) {
            return;
        }
        BigDecimal ema12 = calcEMA(kLines, 12);
        BigDecimal ema26 = calcEMA(kLines, 26);
        if (ema12 == null || ema26 == null) {
            return;
        }
        macdDif = ema12.subtract(ema26).setScale(4, RoundingMode.HALF_UP);
        macdDea = macdDif.multiply(new BigDecimal("0.8")).setScale(4, RoundingMode.HALF_UP);
        macdBar = macdDif.subtract(macdDea).multiply(new BigDecimal("2")).setScale(4, RoundingMode.HALF_UP);
    }

    // ======================== 静态计算工具方法 ========================

    /**
     * 计算简单移动平均线
     *
     * @param kLines K线列表（按日期升序）
     * @param period 周期
     * @return 最新MA值，数据不足时返回null
     */
    public static BigDecimal calcMA(List<KLineData> kLines, int period) {
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
     * 计算RSI
     *
     * @param kLines K线列表（按日期升序）
     * @param period 周期（通常为14）
     * @return RSI值（0-100），数据不足时返回null
     */
    public static BigDecimal calcRSI(List<KLineData> kLines, int period) {
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
        return new BigDecimal("100")
                .subtract(new BigDecimal("100").divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP));
    }

    private static BigDecimal calcEMA(List<KLineData> kLines, int period) {
        if (kLines == null || kLines.size() < period) {
            return null;
        }
        BigDecimal multiplier = new BigDecimal("2")
                .divide(new BigDecimal(period + 1), 6, RoundingMode.HALF_UP);
        BigDecimal ema = kLines.get(kLines.size() - period).getClosePrice();
        for (int i = kLines.size() - period + 1; i < kLines.size(); i++) {
            BigDecimal price = kLines.get(i).getClosePrice();
            ema = price.multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)))
                    .setScale(4, RoundingMode.HALF_UP);
        }
        return ema;
    }
}
