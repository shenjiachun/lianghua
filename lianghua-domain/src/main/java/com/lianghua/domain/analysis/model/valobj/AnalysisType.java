package com.lianghua.domain.analysis.model.valobj;

public enum AnalysisType {
    TREND("趋势分析"),
    SENTIMENT("情绪分析"),
    RISK("风险评估"),
    TECHNICAL("技术分析"),
    FUNDAMENTAL("基本面分析");

    private final String description;

    AnalysisType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
