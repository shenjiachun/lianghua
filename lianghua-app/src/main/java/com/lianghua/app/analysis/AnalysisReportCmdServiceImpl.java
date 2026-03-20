package com.lianghua.app.analysis;

import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.Assert;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lianghua.client.api.IAnalysisReportCmdService;
import com.lianghua.client.command.market.AnalysisReportGenerateCmd;
import com.lianghua.client.dto.analysis.AnalysisReportDTO;
import com.lianghua.client.dto.analysis.TechnicalIndicatorsDTO;
import com.lianghua.domain.analysis.gateway.AIAnalysisGateway;
import com.lianghua.domain.analysis.gateway.AnalysisReportGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import com.lianghua.domain.analysis.service.AnalysisDomainService;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.KLineData;
import com.lianghua.domain.market.model.entity.MarketData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 量化分析报告生成命令服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisReportCmdServiceImpl implements IAnalysisReportCmdService {

    private final MarketDataGateway marketDataGateway;
    private final AIAnalysisGateway aiAnalysisGateway;
    private final AnalysisReportGateway analysisReportGateway;
    private final AnalysisDomainService analysisDomainService;

    @Override
    public SingleResponse<AnalysisReportDTO> generateReport(AnalysisReportGenerateCmd cmd) {
        Assert.notNull(cmd.getSymbol(), "股票代码不能为空");
        Assert.isTrue(!cmd.getSymbol().isBlank(), "股票代码不能为空");
        Assert.notNull(cmd.getAiProvider(), "AI提供商不能为空");
        Assert.isTrue(!cmd.getAiProvider().isBlank(), "AI提供商不能为空");

        AIProvider aiProvider = AIProvider.fromCode(cmd.getAiProvider());
        int analysisDays = cmd.getAnalysisDays() != null ? cmd.getAnalysisDays() : 30;

        log.info("开始生成量化分析报告，股票代码：{}，AI提供商：{}", cmd.getSymbol(), aiProvider.getName());

        // 1. 获取实时行情数据
        Optional<MarketData> marketDataOpt = marketDataGateway.findBySymbol(cmd.getSymbol());
        if (marketDataOpt.isEmpty()) {
            return SingleResponse.buildFailure("NOT_FOUND", "未找到股票数据: " + cmd.getSymbol());
        }
        MarketData marketData = marketDataOpt.get();

        // 2. 获取历史K线数据
        List<KLineData> kLines = marketDataGateway.findKLineData(cmd.getSymbol(), analysisDays);

        // 3. 计算技术指标（现由 TechnicalIndicators 静态工厂完成）
        TechnicalIndicators indicators = TechnicalIndicators.calculate(kLines);

        // 4. 构建AI提示词
        String prompt = analysisDomainService.buildAnalysisPrompt(marketData, kLines);

        // 5. 调用AI大模型分析
        String aiContent = aiAnalysisGateway.generateAnalysis(aiProvider, prompt);
        log.info("AI分析完成，股票代码：{}，内容长度：{}", cmd.getSymbol(), aiContent.length());

        // 6. 解析AI返回的结构化数据
        ParsedAnalysis parsed = parseAIContent(aiContent, marketData);

        // 7. 创建报告实体（现由 AnalysisReport 静态工厂完成，保证聚合根一致性）
        AnalysisReport report = AnalysisReport.create(
                cmd.getSymbol(),
                marketData.getStockName(),
                aiProvider,
                aiProvider.getDefaultModel(),
                aiContent,
                indicators,
                parsed.overallScore,
                parsed.recommendation,
                parsed.targetPrice,
                parsed.technicalSummary,
                parsed.fundamentalSummary,
                parsed.riskWarnings
        );

        // 8. 持久化报告
        analysisReportGateway.save(report);

        log.info("量化分析报告生成成功，reportId：{}，摘要：{}", report.getReportId(), report.getAnalysisSummary());
        return SingleResponse.of(toDTO(report, indicators));
    }

    /**
     * 解析AI返回内容（JSON格式）
     */
    private ParsedAnalysis parseAIContent(String aiContent, MarketData marketData) {
        ParsedAnalysis parsed = new ParsedAnalysis();
        try {
            // 尝试从AI返回内容中提取JSON
            String jsonStr = extractJson(aiContent);
            if (jsonStr != null) {
                JSONObject json = JSON.parseObject(jsonStr);
                parsed.overallScore = json.getIntValue("overallScore", 50);
                String rec = json.getString("recommendation");
                parsed.recommendation = rec != null ? Recommendation.valueOf(rec) : Recommendation.HOLD;
                parsed.targetPrice = json.getBigDecimal("targetPrice");
                parsed.technicalSummary = json.getString("technicalSummary");
                parsed.fundamentalSummary = json.getString("fundamentalSummary");
                List<String> warnings = json.getList("riskWarnings", String.class);
                parsed.riskWarnings = warnings != null ? warnings : new ArrayList<>();
            }
        } catch (Exception e) {
            log.warn("解析AI返回内容失败，使用默认值，错误：{}", e.getMessage());
        }
        // 默认值兜底
        if (parsed.overallScore == 0) {
            parsed.overallScore = 50;
        }
        if (parsed.recommendation == null) {
            parsed.recommendation = Recommendation.fromScore(parsed.overallScore);
        }
        if (parsed.targetPrice == null && marketData.getCurrentPrice() != null) {
            parsed.targetPrice = marketData.getCurrentPrice()
                    .multiply(new BigDecimal("1.10"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }
        if (parsed.technicalSummary == null) {
            parsed.technicalSummary = "技术面分析进行中";
        }
        if (parsed.fundamentalSummary == null) {
            parsed.fundamentalSummary = "基本面分析进行中";
        }
        if (parsed.riskWarnings == null) {
            parsed.riskWarnings = List.of("市场有风险，投资需谨慎");
        }
        return parsed;
    }

    /**
     * 从文本中提取JSON内容
     */
    private String extractJson(String content) {
        if (content == null) return null;
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return null;
    }

    private AnalysisReportDTO toDTO(AnalysisReport report, TechnicalIndicators indicators) {
        AnalysisReportDTO dto = new AnalysisReportDTO();
        dto.setReportId(report.getReportId());
        dto.setSymbol(report.getSymbol());
        dto.setStockName(report.getStockName());
        dto.setAiProvider(report.getAiProvider() != null ? report.getAiProvider().getCode() : null);
        dto.setAiModel(report.getAiModel());
        dto.setOverallScore(report.getOverallScore());
        dto.setRecommendation(report.getRecommendation() != null
                ? report.getRecommendation().getCode() : null);
        dto.setTargetPrice(report.getTargetPrice());
        dto.setTechnicalSummary(report.getTechnicalSummary());
        dto.setFundamentalSummary(report.getFundamentalSummary());
        dto.setAiAnalysisContent(report.getAiAnalysisContent());
        dto.setRiskWarnings(report.getRiskWarnings());
        dto.setCreatedAt(report.getCreatedAt());
        if (indicators != null) {
            TechnicalIndicatorsDTO indicatorsDTO = new TechnicalIndicatorsDTO();
            indicatorsDTO.setMa5(indicators.getMa5());
            indicatorsDTO.setMa10(indicators.getMa10());
            indicatorsDTO.setMa20(indicators.getMa20());
            indicatorsDTO.setMa60(indicators.getMa60());
            indicatorsDTO.setMacdDif(indicators.getMacdDif());
            indicatorsDTO.setMacdDea(indicators.getMacdDea());
            indicatorsDTO.setMacdBar(indicators.getMacdBar());
            indicatorsDTO.setRsi14(indicators.getRsi14());
            indicatorsDTO.setBollingerUpper(indicators.getBollingerUpper());
            indicatorsDTO.setBollingerMiddle(indicators.getBollingerMiddle());
            indicatorsDTO.setBollingerLower(indicators.getBollingerLower());
            dto.setTechnicalIndicators(indicatorsDTO);
        }
        return dto;
    }

    private static class ParsedAnalysis {
        int overallScore;
        Recommendation recommendation;
        BigDecimal targetPrice;
        String technicalSummary;
        String fundamentalSummary;
        List<String> riskWarnings;
    }
}
