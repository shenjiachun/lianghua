package com.lianghua.domain.analysis.model.valobj;

public enum AIModel {
    QWEN("qwen-plus", "通义千问"),
    QWEN_TURBO("qwen-turbo", "通义千问Turbo"),
    DOUBAO("doubao-pro-32k", "豆包Pro"),
    DOUBAO_LITE("doubao-lite-32k", "豆包Lite");

    private final String modelId;
    private final String description;

    AIModel(String modelId, String description) {
        this.modelId = modelId;
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public String getDescription() {
        return description;
    }
}
