package com.lianghua.domain.analysis.model.valobj;

public enum TradingSuggestion {
    STRONG_BUY("强烈买入"),
    BUY("买入"),
    HOLD("持有"),
    SELL("卖出"),
    STRONG_SELL("强烈卖出"),
    NEUTRAL("中性");

    private final String description;

    TradingSuggestion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
