package com.lianghua.domain.analysis.model.entity;

import com.lianghua.domain.analysis.model.valobj.AIModel;
import com.lianghua.domain.analysis.model.valobj.AnalysisType;
import com.lianghua.domain.analysis.model.valobj.TradingSuggestion;
import com.lianghua.domain.market.model.valobj.StockCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AnalysisResult {
    private String id;
    private StockCode stockCode;
    private AnalysisType analysisType;
    private AIModel aiModel;
    private String prompt;
    private String content;
    private TradingSuggestion suggestion;
    private BigDecimal confidenceScore;
    private LocalDateTime createdAt;

    private AnalysisResult() {}

    public static AnalysisResult create(String stockCode, AnalysisType type, AIModel model,
                                        String prompt, String content,
                                        TradingSuggestion suggestion, BigDecimal confidence) {
        AnalysisResult result = new AnalysisResult();
        result.id = UUID.randomUUID().toString();
        result.stockCode = StockCode.of(stockCode);
        result.analysisType = type;
        result.aiModel = model;
        result.prompt = prompt;
        result.content = content;
        result.suggestion = suggestion;
        result.confidenceScore = confidence;
        result.createdAt = LocalDateTime.now();
        return result;
    }

    public static AnalysisResult reconstruct(String id, String stockCode, AnalysisType type,
                                              AIModel model, String prompt, String content,
                                              TradingSuggestion suggestion, BigDecimal confidence,
                                              LocalDateTime createdAt) {
        AnalysisResult result = new AnalysisResult();
        result.id = id;
        result.stockCode = StockCode.of(stockCode);
        result.analysisType = type;
        result.aiModel = model;
        result.prompt = prompt;
        result.content = content;
        result.suggestion = suggestion;
        result.confidenceScore = confidence;
        result.createdAt = createdAt;
        return result;
    }

    public boolean isHighConfidence() {
        return confidenceScore != null && confidenceScore.compareTo(new BigDecimal("0.7")) >= 0;
    }
}
