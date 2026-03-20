package com.lianghua.domain.analysis.model.entity;

import com.alibaba.cola.exception.Assert;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 量化分析报告领域实体（聚合根）
 * 充血模型：包含报告创建、有效性判断、投资建议等领域行为
 */
@Data
public class AnalysisReport {

    private Long id;

    /** 报告唯一ID */
    private String reportId;

    /** 股票代码 */
    private String symbol;

    /** 股票名称 */
    private String stockName;

    /** AI模型提供商 */
    private AIProvider aiProvider;

    /** AI模型名称 */
    private String aiModel;

    /** 综合评分（0-100） */
    private Integer overallScore;

    /** 投资建议 */
    private Recommendation recommendation;

    /** 目标价格 */
    private BigDecimal targetPrice;

    /** 技术面分析摘要 */
    private String technicalSummary;

    /** 基本面分析摘要 */
    private String fundamentalSummary;

    /** AI分析全文 */
    private String aiAnalysisContent;

    /** 技术指标快照 */
    private TechnicalIndicators technicalIndicators;

    /** 风险提示列表 */
    private List<String> riskWarnings;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    // ======================== 静态工厂方法 ========================

    /**
     * 创建新的分析报告（业务入口，保证聚合根的一致性）
     */
    public static AnalysisReport create(String symbol, String stockName,
                                        AIProvider aiProvider, String aiModel,
                                        String aiContent, TechnicalIndicators indicators,
                                        int overallScore, Recommendation recommendation,
                                        BigDecimal targetPrice,
                                        String technicalSummary, String fundamentalSummary,
                                        List<String> riskWarnings) {
        Assert.notNull(symbol, "股票代码不能为空");
        Assert.notNull(aiProvider, "AI提供商不能为空");
        Assert.isTrue(overallScore >= 0 && overallScore <= 100, "评分必须在0到100之间（含0和100）");

        AnalysisReport report = new AnalysisReport();
        report.reportId = UUID.randomUUID().toString().replace("-", "");
        report.symbol = symbol;
        report.stockName = stockName;
        report.aiProvider = aiProvider;
        report.aiModel = aiModel;
        report.overallScore = overallScore;
        report.recommendation = recommendation;
        report.targetPrice = targetPrice;
        report.technicalSummary = technicalSummary;
        report.fundamentalSummary = fundamentalSummary;
        report.aiAnalysisContent = aiContent;
        report.technicalIndicators = indicators;
        report.riskWarnings = riskWarnings != null ? riskWarnings : new ArrayList<>();
        report.createdAt = LocalDateTime.now();
        report.updatedAt = LocalDateTime.now();
        return report;
    }

    // ======================== 领域行为 ========================

    /**
     * 判断是否为高可信度报告（评分 >= 70）
     */
    public boolean isHighConfidence() {
        return overallScore != null && overallScore >= 70;
    }

    /**
     * 判断是否推荐买入
     */
    public boolean shouldBuy() {
        return Recommendation.BUY.equals(recommendation);
    }

    /**
     * 判断是否推荐卖出
     */
    public boolean shouldSell() {
        return Recommendation.SELL.equals(recommendation);
    }

    /**
     * 判断是否存在高风险（风险提示条数 >= 3）
     */
    public boolean isHighRisk() {
        return riskWarnings != null && riskWarnings.size() >= 3;
    }

    /**
     * 判断技术面信号是否与AI建议一致
     * 若技术信号为多头且建议为BUY，或技术信号为空头且建议为SELL，则认为一致
     */
    public boolean isTechnicalSignalConsistent() {
        if (technicalIndicators == null || recommendation == null) {
            return false;
        }
        String signal = technicalIndicators.getOverallSignal();
        return (shouldBuy() && "BULLISH".equals(signal))
                || (shouldSell() && "BEARISH".equals(signal));
    }

    /**
     * 生成报告摘要（用于日志、通知等场景）
     */
    public String getAnalysisSummary() {
        return String.format("[%s] %s(%s) - 评分：%d，建议：%s，目标价：%s%s",
                aiProvider != null ? aiProvider.getName() : "N/A",
                stockName != null ? stockName : "N/A",
                symbol != null ? symbol : "N/A",
                overallScore != null ? overallScore : 0,
                recommendation != null ? recommendation.getDesc() : "N/A",
                targetPrice != null ? String.format("%.2f元", targetPrice) : "N/A",
                isHighRisk() ? "【高风险】" : "");
    }
}
