package com.lianghua.domain;

import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分析报告聚合根充血模式单元测试
 * 验证 AnalysisReport 静态工厂方法和领域行为的正确性
 */
class AnalysisReportRichModelTest {

    private AnalysisReport buildReport(int score, Recommendation recommendation) {
        TechnicalIndicators indicators = new TechnicalIndicators();
        indicators.setMa5(new BigDecimal("15"));
        indicators.setMa10(new BigDecimal("14"));
        indicators.setMa20(new BigDecimal("13"));
        indicators.setMacdDif(new BigDecimal("0.05"));
        indicators.setMacdDea(new BigDecimal("0.03"));
        indicators.setRsi14(new BigDecimal("55"));

        return AnalysisReport.create(
                "000001.SZ",
                "平安银行",
                AIProvider.ALIYUN,
                "qwen-turbo",
                "AI分析全文内容",
                indicators,
                score,
                recommendation,
                new BigDecimal("13.50"),
                "技术面多头排列",
                "估值合理",
                Arrays.asList("市场整体风险需关注")
        );
    }

    @Test
    void testCreateReportGeneratesUniqueId() {
        AnalysisReport r1 = buildReport(75, Recommendation.BUY);
        AnalysisReport r2 = buildReport(75, Recommendation.BUY);
        assertNotNull(r1.getReportId());
        assertNotNull(r2.getReportId());
        assertNotEquals(r1.getReportId(), r2.getReportId());
    }

    @Test
    void testCreateReportSetsTimestamps() {
        AnalysisReport report = buildReport(75, Recommendation.BUY);
        assertNotNull(report.getCreatedAt());
        assertNotNull(report.getUpdatedAt());
    }

    @Test
    void testCreateReportValidatesScore() {
        assertThrows(Exception.class, () -> AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", null, -1, Recommendation.BUY,
                new BigDecimal("13.50"), "tech", "fund",
                Collections.emptyList()));

        assertThrows(Exception.class, () -> AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", null, 101, Recommendation.BUY,
                new BigDecimal("13.50"), "tech", "fund",
                Collections.emptyList()));
    }

    @Test
    void testCreateReportValidatesSymbol() {
        assertThrows(Exception.class, () -> AnalysisReport.create(
                null, "平安银行", AIProvider.ALIYUN, "model",
                "content", null, 75, Recommendation.BUY,
                new BigDecimal("13.50"), "tech", "fund",
                Collections.emptyList()));
    }

    @Test
    void testIsHighConfidence() {
        AnalysisReport highConf = buildReport(75, Recommendation.BUY);
        assertTrue(highConf.isHighConfidence());

        AnalysisReport lowConf = buildReport(60, Recommendation.HOLD);
        assertFalse(lowConf.isHighConfidence());
    }

    @Test
    void testShouldBuyAndSell() {
        AnalysisReport buyReport = buildReport(80, Recommendation.BUY);
        assertTrue(buyReport.shouldBuy());
        assertFalse(buyReport.shouldSell());

        AnalysisReport sellReport = buildReport(20, Recommendation.SELL);
        assertFalse(sellReport.shouldBuy());
        assertTrue(sellReport.shouldSell());
    }

    @Test
    void testIsHighRisk() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        AnalysisReport lowRisk = AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", indicators, 70, Recommendation.BUY,
                new BigDecimal("13.50"), "tech", "fund",
                Arrays.asList("风险1", "风险2"));
        assertFalse(lowRisk.isHighRisk());

        AnalysisReport highRisk = AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", indicators, 40, Recommendation.SELL,
                new BigDecimal("13.50"), "tech", "fund",
                Arrays.asList("风险1", "风险2", "风险3"));
        assertTrue(highRisk.isHighRisk());
    }

    @Test
    void testIsTechnicalSignalConsistent() {
        TechnicalIndicators bullishIndicators = new TechnicalIndicators();
        bullishIndicators.setMa5(new BigDecimal("15"));
        bullishIndicators.setMa10(new BigDecimal("14"));
        bullishIndicators.setMa20(new BigDecimal("13"));
        bullishIndicators.setMacdDif(new BigDecimal("0.05"));
        bullishIndicators.setMacdDea(new BigDecimal("0.03"));
        bullishIndicators.setRsi14(new BigDecimal("55")); // neutral RSI

        AnalysisReport consistentReport = AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", bullishIndicators, 80, Recommendation.BUY,
                new BigDecimal("13.50"), "tech", "fund",
                Collections.emptyList());
        // bullish indicators + BUY recommendation = consistent
        assertTrue(consistentReport.isTechnicalSignalConsistent());
    }

    @Test
    void testGetAnalysisSummary() {
        AnalysisReport report = buildReport(80, Recommendation.BUY);
        String summary = report.getAnalysisSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("平安银行"));
        assertTrue(summary.contains("000001.SZ"));
        assertTrue(summary.contains("80"));
        assertTrue(summary.contains("买入"));
    }

    @Test
    void testGetAnalysisSummaryHighRisk() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        AnalysisReport report = AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", indicators, 30, Recommendation.SELL,
                new BigDecimal("10.00"), "tech", "fund",
                Arrays.asList("风险1", "风险2", "风险3", "风险4"));
        assertTrue(report.getAnalysisSummary().contains("高风险"));
    }

    @Test
    void testNullRiskWarningsDefaultsToEmpty() {
        TechnicalIndicators indicators = new TechnicalIndicators();
        AnalysisReport report = AnalysisReport.create(
                "000001.SZ", "平安银行", AIProvider.ALIYUN, "model",
                "content", indicators, 70, Recommendation.HOLD,
                new BigDecimal("13.00"), "tech", "fund", null);
        assertNotNull(report.getRiskWarnings());
        assertTrue(report.getRiskWarnings().isEmpty());
    }
}
