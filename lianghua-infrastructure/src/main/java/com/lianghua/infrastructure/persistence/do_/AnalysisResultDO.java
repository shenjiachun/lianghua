package com.lianghua.infrastructure.persistence.do_;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnalysisResultDO {
    private String id;
    private String stockCode;
    private String analysisType;
    private String aiModel;
    private String prompt;
    private String content;
    private String suggestion;
    private BigDecimal confidenceScore;
    private LocalDateTime createdAt;
}
