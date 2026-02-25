package com.lianghua.app.analysis;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.Assert;
import com.lianghua.client.api.IAnalysisReportQueryService;
import com.lianghua.client.dto.analysis.AnalysisReportDTO;
import com.lianghua.client.dto.analysis.TechnicalIndicatorsDTO;
import com.lianghua.client.query.analysis.AnalysisReportBySymbolQuery;
import com.lianghua.client.query.analysis.AnalysisReportListQuery;
import com.lianghua.domain.analysis.gateway.AnalysisReportGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分析报告查询服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisReportQueryServiceImpl implements IAnalysisReportQueryService {

    private final AnalysisReportGateway analysisReportGateway;

    @Override
    public SingleResponse<AnalysisReportDTO> getReportBySymbol(AnalysisReportBySymbolQuery query) {
        Assert.notNull(query.getSymbol(), "股票代码不能为空");
        Assert.isTrue(!query.getSymbol().isBlank(), "股票代码不能为空");
        AIProvider aiProvider = null;
        if (query.getAiProvider() != null && !query.getAiProvider().isBlank()) {
            aiProvider = AIProvider.fromCode(query.getAiProvider());
        }
        Optional<AnalysisReport> reportOpt = analysisReportGateway.findLatestBySymbol(
                query.getSymbol(), aiProvider);
        if (reportOpt.isEmpty()) {
            return SingleResponse.buildFailure("NOT_FOUND", "未找到分析报告: " + query.getSymbol());
        }
        return SingleResponse.of(toDTO(reportOpt.get()));
    }

    @Override
    public MultiResponse<AnalysisReportDTO> listReports(AnalysisReportListQuery query) {
        int pageNum = query.getPageIndex() > 0 ? query.getPageIndex() : 1;
        int pageSize = query.getPageSize() > 0 ? query.getPageSize() : 20;
        List<AnalysisReport> list = analysisReportGateway.findList(
                query.getSymbol(), query.getAiProvider(), query.getRecommendation(),
                pageNum, pageSize);
        List<AnalysisReportDTO> dtoList = list.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    private AnalysisReportDTO toDTO(AnalysisReport entity) {
        AnalysisReportDTO dto = new AnalysisReportDTO();
        dto.setReportId(entity.getReportId());
        dto.setSymbol(entity.getSymbol());
        dto.setStockName(entity.getStockName());
        dto.setAiProvider(entity.getAiProvider() != null ? entity.getAiProvider().getCode() : null);
        dto.setAiModel(entity.getAiModel());
        dto.setOverallScore(entity.getOverallScore());
        dto.setRecommendation(entity.getRecommendation() != null
                ? entity.getRecommendation().getCode() : null);
        dto.setTargetPrice(entity.getTargetPrice());
        dto.setTechnicalSummary(entity.getTechnicalSummary());
        dto.setFundamentalSummary(entity.getFundamentalSummary());
        dto.setAiAnalysisContent(entity.getAiAnalysisContent());
        dto.setRiskWarnings(entity.getRiskWarnings());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getTechnicalIndicators() != null) {
            dto.setTechnicalIndicators(toIndicatorsDTO(entity.getTechnicalIndicators()));
        }
        return dto;
    }

    private TechnicalIndicatorsDTO toIndicatorsDTO(TechnicalIndicators indicators) {
        TechnicalIndicatorsDTO dto = new TechnicalIndicatorsDTO();
        dto.setMa5(indicators.getMa5());
        dto.setMa10(indicators.getMa10());
        dto.setMa20(indicators.getMa20());
        dto.setMa60(indicators.getMa60());
        dto.setMacdDif(indicators.getMacdDif());
        dto.setMacdDea(indicators.getMacdDea());
        dto.setMacdBar(indicators.getMacdBar());
        dto.setRsi14(indicators.getRsi14());
        dto.setBollingerUpper(indicators.getBollingerUpper());
        dto.setBollingerMiddle(indicators.getBollingerMiddle());
        dto.setBollingerLower(indicators.getBollingerLower());
        dto.setKdjK(indicators.getKdjK());
        dto.setKdjD(indicators.getKdjD());
        dto.setKdjJ(indicators.getKdjJ());
        dto.setVolumeRatio(indicators.getVolumeRatio());
        return dto;
    }
}
