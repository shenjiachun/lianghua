package com.lianghua.client.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnalysisResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String stockCode;
    /** TREND, SENTIMENT, RISK, TECHNICAL, FUNDAMENTAL */
    private String analysisType;
    /** QWEN, DOUBAO */
    private String aiModel;
    /** AI analysis content */
    private String content;
    /** BUY, SELL, HOLD */
    private String suggestion;
    private BigDecimal confidenceScore;
    private LocalDateTime createdAt;
}
