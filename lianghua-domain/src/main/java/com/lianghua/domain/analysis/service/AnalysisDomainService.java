package com.lianghua.domain.analysis.service;

import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.domain.market.service.MarketDataDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 量化分析领域服务
 */
@Service
@RequiredArgsConstructor
public class AnalysisDomainService {

    private final MarketDataDomainService marketDataDomainService;

    /**
     * 构建AI分析提示词
     */
    public String buildAnalysisPrompt(MarketData marketData, List<KLineData> kLines) {
        TechnicalIndicators indicators = calcTechnicalIndicators(kLines);
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名专业的A股量化分析师，请对以下股票进行全面分析并给出投资建议。\n\n");
        prompt.append("## 股票基本信息\n");
        prompt.append(String.format("- 股票代码：%s\n", marketData.getStockSymbol().getFullSymbol()));
        prompt.append(String.format("- 股票名称：%s\n", marketData.getStockName()));
        prompt.append(String.format("- 当前价格：%.2f 元\n", marketData.getCurrentPrice()));
        prompt.append(String.format("- 今日涨跌幅：%.2f%%\n", marketData.getChangePercent()));
        prompt.append(String.format("- 成交量：%d 手\n", marketData.getVolume()));
        prompt.append(String.format("- 换手率：%.2f%%\n", marketData.getTurnoverRate()));
        if (marketData.getPeRatiaTtm() != null) {
            prompt.append(String.format("- 市盈率（TTM）：%.2f\n", marketData.getPeRatiaTtm()));
        }
        if (marketData.getPbRatio() != null) {
            prompt.append(String.format("- 市净率：%.2f\n", marketData.getPbRatio()));
        }
        prompt.append("\n## 技术指标\n");
        if (indicators.getMa5() != null) {
            prompt.append(String.format("- MA5：%.2f\n", indicators.getMa5()));
        }
        if (indicators.getMa10() != null) {
            prompt.append(String.format("- MA10：%.2f\n", indicators.getMa10()));
        }
        if (indicators.getMa20() != null) {
            prompt.append(String.format("- MA20：%.2f\n", indicators.getMa20()));
        }
        if (indicators.getMa60() != null) {
            prompt.append(String.format("- MA60：%.2f\n", indicators.getMa60()));
        }
        if (indicators.getRsi14() != null) {
            prompt.append(String.format("- RSI(14)：%.2f\n", indicators.getRsi14()));
        }
        prompt.append("\n请从以下维度进行分析：\n");
        prompt.append("1. 技术面分析（均线形态、MACD、RSI、量价关系等）\n");
        prompt.append("2. 基本面分析（估值水平、行业地位等）\n");
        prompt.append("3. 风险提示\n");
        prompt.append("4. 综合评分（0-100分）和投资建议（BUY/HOLD/SELL）\n");
        prompt.append("5. 目标价位\n\n");
        prompt.append("请以JSON格式返回，包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"overallScore\": <评分>,\n");
        prompt.append("  \"recommendation\": \"BUY/HOLD/SELL\",\n");
        prompt.append("  \"targetPrice\": <目标价>,\n");
        prompt.append("  \"technicalSummary\": \"技术面分析摘要\",\n");
        prompt.append("  \"fundamentalSummary\": \"基本面分析摘要\",\n");
        prompt.append("  \"riskWarnings\": [\"风险1\", \"风险2\"],\n");
        prompt.append("  \"fullAnalysis\": \"完整分析内容\"\n");
        prompt.append("}\n");
        return prompt.toString();
    }

    /**
     * 计算技术指标
     */
    public TechnicalIndicators calcTechnicalIndicators(List<KLineData> kLines) {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setMa5(marketDataDomainService.calcMA(kLines, 5));
        indicators.setMa10(marketDataDomainService.calcMA(kLines, 10));
        indicators.setMa20(marketDataDomainService.calcMA(kLines, 20));
        indicators.setMa60(marketDataDomainService.calcMA(kLines, 60));
        indicators.setRsi14(marketDataDomainService.calcRSI(kLines, 14));
        calcBollinger(kLines, indicators);
        calcMACD(kLines, indicators);
        return indicators;
    }

    /**
     * 计算布林带
     */
    private void calcBollinger(List<KLineData> kLines, TechnicalIndicators indicators) {
        int period = 20;
        if (kLines == null || kLines.size() < period) {
            return;
        }
        BigDecimal ma20 = indicators.getMa20();
        if (ma20 == null) {
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
        indicators.setBollingerMiddle(ma20);
        indicators.setBollingerUpper(ma20.add(twoStdDev).setScale(2, RoundingMode.HALF_UP));
        indicators.setBollingerLower(ma20.subtract(twoStdDev).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * 计算MACD（12,26,9）
     */
    private void calcMACD(List<KLineData> kLines, TechnicalIndicators indicators) {
        if (kLines == null || kLines.size() < 26) {
            return;
        }
        // 计算EMA12和EMA26
        BigDecimal ema12 = calcEMA(kLines, 12);
        BigDecimal ema26 = calcEMA(kLines, 26);
        if (ema12 == null || ema26 == null) {
            return;
        }
        BigDecimal dif = ema12.subtract(ema26).setScale(4, RoundingMode.HALF_UP);
        indicators.setMacdDif(dif);
        // DEA为DIF的9日EMA的近似值（简化计算）
        indicators.setMacdDea(dif.multiply(new BigDecimal("0.8")).setScale(4, RoundingMode.HALF_UP));
        BigDecimal macdBar = dif.subtract(indicators.getMacdDea())
                .multiply(new BigDecimal("2")).setScale(4, RoundingMode.HALF_UP);
        indicators.setMacdBar(macdBar);
    }

    /**
     * 计算EMA
     */
    private BigDecimal calcEMA(List<KLineData> kLines, int period) {
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

    /**
     * 创建分析报告实体
     */
    public AnalysisReport createReport(String symbol, String stockName,
                                        AIProvider aiProvider, String aiModel,
                                        String aiContent, TechnicalIndicators indicators,
                                        int overallScore, Recommendation recommendation,
                                        BigDecimal targetPrice,
                                        String technicalSummary, String fundamentalSummary,
                                        List<String> riskWarnings) {
        AnalysisReport report = new AnalysisReport();
        report.setReportId(UUID.randomUUID().toString().replace("-", ""));
        report.setSymbol(symbol);
        report.setStockName(stockName);
        report.setAiProvider(aiProvider);
        report.setAiModel(aiModel);
        report.setOverallScore(overallScore);
        report.setRecommendation(recommendation);
        report.setTargetPrice(targetPrice);
        report.setTechnicalSummary(technicalSummary);
        report.setFundamentalSummary(fundamentalSummary);
        report.setAiAnalysisContent(aiContent);
        report.setTechnicalIndicators(indicators);
        report.setRiskWarnings(riskWarnings != null ? riskWarnings : new ArrayList<>());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        return report;
    }
}
