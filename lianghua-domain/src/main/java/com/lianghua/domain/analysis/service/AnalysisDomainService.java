package com.lianghua.domain.analysis.service;

import com.alibaba.cola.exception.BizException;
import com.lianghua.domain.analysis.gateway.AIModelGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisResult;
import com.lianghua.domain.analysis.model.valobj.AIModel;
import com.lianghua.domain.analysis.model.valobj.AnalysisType;
import com.lianghua.domain.analysis.model.valobj.TradingSuggestion;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AnalysisDomainService {

    private final List<AIModelGateway> aiModelGateways;

    public AnalysisDomainService(List<AIModelGateway> aiModelGateways) {
        this.aiModelGateways = aiModelGateways;
    }

    /**
     * Builds the user prompt for AI market analysis.
     */
    public String buildAnalysisPrompt(AnalysisType type, String marketSummary, String extraContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("请对以下股票市场数据进行").append(type.getDescription()).append("：\n\n");
        sb.append(marketSummary);
        if (extraContext != null && !extraContext.isBlank()) {
            sb.append("\n\n额外背景信息：\n").append(extraContext);
        }
        sb.append("\n\n请提供详细分析，并在最后给出交易建议（买入/卖出/持有/观望）和置信度（0-1之间的小数）。");
        sb.append("\n格式示例：\n分析内容: [详细分析]\n建议: [买入/卖出/持有/中性/强烈买入/强烈卖出]\n置信度: [0.0-1.0]");
        return sb.toString();
    }

    /**
     * Returns the system prompt for a given analysis type.
     */
    public String getSystemPrompt(AnalysisType type) {
        return switch (type) {
            case TREND -> "你是一位专业的股票趋势分析师，擅长通过技术指标和价格走势判断市场趋势方向。请用中文提供专业、客观的分析。";
            case SENTIMENT -> "你是一位专业的市场情绪分析师，擅长分析市场情绪和投资者心理对股价的影响。请用中文提供专业、客观的分析。";
            case RISK -> "你是一位专业的风险评估专家，擅长识别股票投资中的各种风险因素。请用中文提供专业、客观的风险评估。";
            case TECHNICAL -> "你是一位专业的技术分析师，擅长运用各种技术指标（如MACD、RSI、布林带等）进行股票分析。请用中文提供专业、客观的技术分析。";
            case FUNDAMENTAL -> "你是一位专业的基本面分析师，擅长评估公司基本面和行业发展前景。请用中文提供专业、客观的基本面分析。";
        };
    }

    /**
     * Parses AI response to extract suggestion and confidence, then creates AnalysisResult.
     */
    public AnalysisResult parseAndCreateResult(String stockCode, AnalysisType type, AIModel model,
                                                String prompt, String aiResponse) {
        TradingSuggestion suggestion = parseSuggestion(aiResponse);
        BigDecimal confidence = parseConfidence(aiResponse);
        return AnalysisResult.create(stockCode, type, model, prompt, aiResponse, suggestion, confidence);
    }

    private TradingSuggestion parseSuggestion(String response) {
        if (response == null) return TradingSuggestion.NEUTRAL;
        String lower = response.toLowerCase();
        if (lower.contains("强烈买入") || lower.contains("strong buy")) return TradingSuggestion.STRONG_BUY;
        if (lower.contains("强烈卖出") || lower.contains("strong sell")) return TradingSuggestion.STRONG_SELL;
        if (lower.contains("买入") || lower.contains("buy")) return TradingSuggestion.BUY;
        if (lower.contains("卖出") || lower.contains("sell")) return TradingSuggestion.SELL;
        if (lower.contains("持有") || lower.contains("hold")) return TradingSuggestion.HOLD;
        return TradingSuggestion.NEUTRAL;
    }

    private BigDecimal parseConfidence(String response) {
        if (response == null) return new BigDecimal("0.5");
        // Try to find "置信度: 0.xx" pattern
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.contains("置信度") || line.toLowerCase().contains("confidence")) {
                String[] parts = line.split("[：:=\\s]+");
                for (String part : parts) {
                    try {
                        BigDecimal val = new BigDecimal(part.trim());
                        if (val.compareTo(BigDecimal.ZERO) >= 0 && val.compareTo(BigDecimal.ONE) <= 0) {
                            return val;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return new BigDecimal("0.6");
    }

    private AIModelGateway getGateway(AIModel model) {
        return aiModelGateways.stream()
                .filter(g -> g.supports(model))
                .findFirst()
                .orElseThrow(() -> new BizException("Unsupported AI model: " + model));
    }

    public AnalysisResult performAnalysis(String stockCode, AnalysisType type, AIModel model,
                                          String marketSummary, String extraContext) {
        String systemPrompt = getSystemPrompt(type);
        String userPrompt = buildAnalysisPrompt(type, marketSummary, extraContext);
        String response = getGateway(model).analyze(model, systemPrompt, userPrompt);
        return parseAndCreateResult(stockCode, type, model, userPrompt, response);
    }
}
