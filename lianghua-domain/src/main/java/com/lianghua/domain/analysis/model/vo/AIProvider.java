package com.lianghua.domain.analysis.model.vo;

/**
 * AI模型提供商枚举
 */
public enum AIProvider {

    /** 阿里云通义千问 */
    ALIYUN("ALIYUN", "阿里云通义千问", "qwen-turbo"),

    /** 字节跳动豆包 */
    BYTEDANCE("BYTEDANCE", "字节跳动豆包", "doubao-pro-4k");

    private final String code;
    private final String name;
    private final String defaultModel;

    AIProvider(String code, String name, String defaultModel) {
        this.code = code;
        this.name = name;
        this.defaultModel = defaultModel;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public static AIProvider fromCode(String code) {
        for (AIProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("不支持的AI提供商: " + code);
    }
}
