package com.lianghua.domain.analysis.gateway;

import com.lianghua.domain.analysis.model.vo.AIProvider;

/**
 * AI分析网关接口（领域层定义，基础设施层实现）
 * 负责调用AI大模型生成量化分析内容
 */
public interface AIAnalysisGateway {

    /**
     * 调用AI大模型生成分析内容
     *
     * @param provider AI提供商
     * @param prompt   分析提示词
     * @return AI生成的分析内容
     */
    String generateAnalysis(AIProvider provider, String prompt);

    /**
     * 判断是否支持该提供商
     */
    boolean supports(AIProvider provider);
}
