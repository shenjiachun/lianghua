package com.lianghua.domain.analysis.model.vo;

/**
 * 投资建议枚举
 */
public enum Recommendation {

    /** 买入 */
    BUY("BUY", "买入"),

    /** 持有 */
    HOLD("HOLD", "持有"),

    /** 卖出 */
    SELL("SELL", "卖出");

    private final String code;
    private final String desc;

    Recommendation(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static Recommendation fromScore(int score) {
        if (score >= 70) {
            return BUY;
        } else if (score >= 40) {
            return HOLD;
        } else {
            return SELL;
        }
    }
}
