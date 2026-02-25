package com.lianghua.infrastructure.analysis.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 量化分析报告持久化对象
 */
@Data
@TableName("analysis_report")
public class AnalysisReportDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String reportId;
    private String symbol;
    private String stockName;
    private String aiProvider;
    private String aiModel;
    private Integer overallScore;
    private String recommendation;
    private BigDecimal targetPrice;
    private String technicalSummary;
    private String fundamentalSummary;
    private String aiAnalysisContent;

    /** 技术指标JSON（序列化存储） */
    private String technicalIndicatorsJson;

    /** 风险提示JSON（序列化存储） */
    private String riskWarningsJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
