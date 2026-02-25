package com.lianghua.app.command;

import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.command.AnalyzeMarketCmd;
import com.lianghua.client.dto.AnalysisResultDTO;
import com.lianghua.domain.analysis.gateway.AnalysisResultGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisResult;
import com.lianghua.domain.analysis.model.valobj.AIModel;
import com.lianghua.domain.analysis.model.valobj.AnalysisType;
import com.lianghua.domain.analysis.service.AnalysisDomainService;
import com.lianghua.domain.market.gateway.MarketDataGateway;
import com.lianghua.domain.market.model.entity.MarketData;
import com.lianghua.domain.market.service.MarketDataDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalyzeMarketCmdExe {

    private final MarketDataGateway marketDataGateway;
    private final AnalysisResultGateway analysisResultGateway;
    private final MarketDataDomainService marketDataDomainService;
    private final AnalysisDomainService analysisDomainService;

    public SingleResponse<AnalysisResultDTO> execute(AnalyzeMarketCmd cmd) {
        LocalDate endDate = LocalDate.now();
        int days = cmd.getLookbackDays() != null ? cmd.getLookbackDays() : 30;
        LocalDate startDate = endDate.minusDays(days);

        List<MarketData> marketDataList = marketDataGateway.findByStockCode(
                cmd.getStockCode(), startDate, endDate);

        String summary = marketDataDomainService.generateMarketSummary(marketDataList);

        AnalysisType type = AnalysisType.valueOf(cmd.getAnalysisType());
        AIModel model = AIModel.valueOf(cmd.getAiModel());

        AnalysisResult result = analysisDomainService.performAnalysis(
                cmd.getStockCode(), type, model, summary, cmd.getExtraContext());

        analysisResultGateway.save(result);

        return SingleResponse.of(toDTO(result));
    }

    private AnalysisResultDTO toDTO(AnalysisResult result) {
        AnalysisResultDTO dto = new AnalysisResultDTO();
        dto.setId(result.getId());
        dto.setStockCode(result.getStockCode().getFullCode());
        dto.setAnalysisType(result.getAnalysisType().name());
        dto.setAiModel(result.getAiModel().name());
        dto.setContent(result.getContent());
        dto.setSuggestion(result.getSuggestion() != null ? result.getSuggestion().name() : null);
        dto.setConfidenceScore(result.getConfidenceScore());
        dto.setCreatedAt(result.getCreatedAt());
        return dto;
    }
}
