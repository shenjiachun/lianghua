package com.lianghua.domain.analysis.service;

import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 量化分析领域服务
 * 充血模型重构后，仅保留跨实体的协调逻辑：
 * - 技术指标计算已委托给 TechnicalIndicators.calculate() 静态工厂方法
 * - 报告创建已委托给 AnalysisReport.create() 静态工厂方法
 * - 此服务专注于 AI 提示词构建（需要聚合 MarketData + TechnicalIndicators 信息）
 */
@Service
public class AnalysisDomainService {

    /**
     * 构建AI分析提示词
     * 该方法需要跨聚合地协调 MarketData 和 TechnicalIndicators，保留在领域服务中。
     *
     * @param marketData 市场行情数据
     * @param kLines     历史K线列表（按日期升序）
     * @return 结构化的AI分析提示词
     */
    public String buildAnalysisPrompt(MarketData marketData, List<KLineData> kLines) {
        TechnicalIndicators indicators = TechnicalIndicators.calculate(kLines);
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一名专业的A股量化分析师，请对以下股票进行全面分析并给出投资建议。\n\n");
        prompt.append("## 股票基本信息\n");
        prompt.append(marketData.buildBasicInfoSummary()).append("\n");
        prompt.append("\n## 技术指标\n");
        if (indicators.getMa5() != null) {
            prompt.append(String.format("- MA5：%.2f\n", indicators.getMa5()));
        }
        if (indicators.getMa10() != null) {
            prompt.append(String.format("- MA10：%.2f\n", indicators.getMa10()));
        }
        if (indicators.getMa20() != null) {
            prompt.append(String.format("- MA20：%.2f\n", indicators.getMa20()));
        }
        if (indicators.getMa60() != null) {
            prompt.append(String.format("- MA60：%.2f\n", indicators.getMa60()));
        }
        if (indicators.getRsi14() != null) {
            prompt.append(String.format("- RSI(14)：%.2f\n", indicators.getRsi14()));
        }
        if (indicators.getMacdDif() != null) {
            prompt.append(String.format("- MACD DIF：%.4f\n", indicators.getMacdDif()));
            prompt.append(String.format("- MACD DEA：%.4f\n", indicators.getMacdDea()));
        }
        prompt.append(String.format("- 综合技术信号：%s\n", indicators.getOverallSignal()));
        if (indicators.isMaBullishAlignment()) {
            prompt.append("- 均线形态：多头排列\n");
        } else if (indicators.isMaBearishAlignment()) {
            prompt.append("- 均线形态：空头排列\n");
        }
        if (indicators.isRsiOverbought()) {
            prompt.append("- RSI提示：超买区域，注意回调风险\n");
        } else if (indicators.isRsiOversold()) {
            prompt.append("- RSI提示：超卖区域，存在反弹机会\n");
        }
        prompt.append("\n请从以下维度进行分析：\n");
        prompt.append("1. 技术面分析（均线形态、MACD、RSI、量价关系等）\n");
        prompt.append("2. 基本面分析（估值水平、行业地位等）\n");
        prompt.append("3. 风险提示\n");
        prompt.append("4. 综合评分（0-100分）和投资建议（BUY/HOLD/SELL）\n");
        prompt.append("5. 目标价位\n\n");
        prompt.append("请以JSON格式返回，包含以下字段：\n");
        prompt.append("{\n");
        prompt.append("  \"overallScore\": <评分>,\n");
        prompt.append("  \"recommendation\": \"BUY/HOLD/SELL\",\n");
        prompt.append("  \"targetPrice\": <目标价>,\n");
        prompt.append("  \"technicalSummary\": \"技术面分析摘要\",\n");
        prompt.append("  \"fundamentalSummary\": \"基本面分析摘要\",\n");
        prompt.append("  \"riskWarnings\": [\"风险1\", \"风险2\"],\n");
        prompt.append("  \"fullAnalysis\": \"完整分析内容\"\n");
        prompt.append("}\n");
        return prompt.toString();
    }
}
