package com.lianghua.domain.analysis.model.entity;

import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 量化分析报告领域实体
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
}
