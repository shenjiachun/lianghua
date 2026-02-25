package com.lianghua.client.dto.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 量化分析报告传输对象
 */
@Data
public class AnalysisReportDTO {

    /** 报告ID */
    private String reportId;

    /** 股票代码 */
    private String symbol;

    /** 股票名称 */
    private String stockName;

    /** AI模型提供商 */
    private String aiProvider;

    /** AI模型名称 */
    private String aiModel;

    /** 综合评分（0-100） */
    private Integer overallScore;

    /** 投资建议（BUY/HOLD/SELL） */
    private String recommendation;

    /** 目标价格 */
    private BigDecimal targetPrice;

    /** 技术面分析摘要 */
    private String technicalSummary;

    /** 基本面分析摘要 */
    private String fundamentalSummary;

    /** AI分析全文 */
    private String aiAnalysisContent;

    /** 技术指标 */
    private TechnicalIndicatorsDTO technicalIndicators;

    /** 风险提示列表 */
    private List<String> riskWarnings;

    /** 报告生成时间 */
    private LocalDateTime createdAt;
}
