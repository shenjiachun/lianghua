package com.lianghua.infrastructure.analysis.gateway;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lianghua.domain.analysis.gateway.AnalysisReportGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.entity.TechnicalIndicators;
import com.lianghua.domain.analysis.model.vo.AIProvider;
import com.lianghua.domain.analysis.model.vo.Recommendation;
import com.lianghua.infrastructure.analysis.dataobject.AnalysisReportDO;
import com.lianghua.infrastructure.analysis.mapper.AnalysisReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分析报告网关实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisReportGatewayImpl implements AnalysisReportGateway {

    private final AnalysisReportMapper analysisReportMapper;

    @Override
    public Optional<AnalysisReport> findById(String reportId) {
        LambdaQueryWrapper<AnalysisReportDO> wrapper = new LambdaQueryWrapper<AnalysisReportDO>()
                .eq(AnalysisReportDO::getReportId, reportId);
        AnalysisReportDO dataObject = analysisReportMapper.selectOne(wrapper);
        return Optional.ofNullable(dataObject).map(this::toEntity);
    }

    @Override
    public Optional<AnalysisReport> findLatestBySymbol(String symbol, AIProvider aiProvider) {
        LambdaQueryWrapper<AnalysisReportDO> wrapper = new LambdaQueryWrapper<AnalysisReportDO>()
                .eq(AnalysisReportDO::getSymbol, symbol)
                .orderByDesc(AnalysisReportDO::getCreatedAt)
                .last("LIMIT 1");
        if (aiProvider != null) {
            wrapper.eq(AnalysisReportDO::getAiProvider, aiProvider.getCode());
        }
        AnalysisReportDO dataObject = analysisReportMapper.selectOne(wrapper);
        return Optional.ofNullable(dataObject).map(this::toEntity);
    }

    @Override
    public List<AnalysisReport> findList(String symbol, String aiProvider, String recommendation,
                                          int pageNum, int pageSize) {
        LambdaQueryWrapper<AnalysisReportDO> wrapper = new LambdaQueryWrapper<AnalysisReportDO>()
                .orderByDesc(AnalysisReportDO::getCreatedAt)
                .last("LIMIT " + ((pageNum - 1) * pageSize) + ", " + pageSize);
        if (symbol != null && !symbol.isBlank()) {
            wrapper.eq(AnalysisReportDO::getSymbol, symbol);
        }
        if (aiProvider != null && !aiProvider.isBlank()) {
            wrapper.eq(AnalysisReportDO::getAiProvider, aiProvider);
        }
        if (recommendation != null && !recommendation.isBlank()) {
            wrapper.eq(AnalysisReportDO::getRecommendation, recommendation);
        }
        return analysisReportMapper.selectList(wrapper).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void save(AnalysisReport report) {
        AnalysisReportDO dataObject = toDO(report);
        dataObject.setCreatedAt(LocalDateTime.now());
        dataObject.setUpdatedAt(LocalDateTime.now());
        analysisReportMapper.insert(dataObject);
    }

    private AnalysisReport toEntity(AnalysisReportDO dataObject) {
        AnalysisReport entity = new AnalysisReport();
        entity.setId(dataObject.getId());
        entity.setReportId(dataObject.getReportId());
        entity.setSymbol(dataObject.getSymbol());
        entity.setStockName(dataObject.getStockName());
        try {
            if (dataObject.getAiProvider() != null) {
                entity.setAiProvider(AIProvider.fromCode(dataObject.getAiProvider()));
            }
        } catch (Exception e) {
            log.warn("解析AI提供商失败: {}", dataObject.getAiProvider());
        }
        entity.setAiModel(dataObject.getAiModel());
        entity.setOverallScore(dataObject.getOverallScore());
        try {
            if (dataObject.getRecommendation() != null) {
                entity.setRecommendation(Recommendation.valueOf(dataObject.getRecommendation()));
            }
        } catch (Exception e) {
            log.warn("解析投资建议失败: {}", dataObject.getRecommendation());
        }
        entity.setTargetPrice(dataObject.getTargetPrice());
        entity.setTechnicalSummary(dataObject.getTechnicalSummary());
        entity.setFundamentalSummary(dataObject.getFundamentalSummary());
        entity.setAiAnalysisContent(dataObject.getAiAnalysisContent());
        entity.setCreatedAt(dataObject.getCreatedAt());
        entity.setUpdatedAt(dataObject.getUpdatedAt());
        // 反序列化技术指标
        if (dataObject.getTechnicalIndicatorsJson() != null) {
            try {
                entity.setTechnicalIndicators(
                        JSON.parseObject(dataObject.getTechnicalIndicatorsJson(), TechnicalIndicators.class));
            } catch (Exception e) {
                log.warn("解析技术指标JSON失败", e);
            }
        }
        // 反序列化风险提示
        if (dataObject.getRiskWarningsJson() != null) {
            try {
                entity.setRiskWarnings(
                        JSON.parseObject(dataObject.getRiskWarningsJson(),
                                new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                log.warn("解析风险提示JSON失败", e);
            }
        }
        return entity;
    }

    private AnalysisReportDO toDO(AnalysisReport entity) {
        AnalysisReportDO dataObject = new AnalysisReportDO();
        dataObject.setId(entity.getId());
        dataObject.setReportId(entity.getReportId());
        dataObject.setSymbol(entity.getSymbol());
        dataObject.setStockName(entity.getStockName());
        dataObject.setAiProvider(entity.getAiProvider() != null
                ? entity.getAiProvider().getCode() : null);
        dataObject.setAiModel(entity.getAiModel());
        dataObject.setOverallScore(entity.getOverallScore());
        dataObject.setRecommendation(entity.getRecommendation() != null
                ? entity.getRecommendation().getCode() : null);
        dataObject.setTargetPrice(entity.getTargetPrice());
        dataObject.setTechnicalSummary(entity.getTechnicalSummary());
        dataObject.setFundamentalSummary(entity.getFundamentalSummary());
        dataObject.setAiAnalysisContent(entity.getAiAnalysisContent());
        if (entity.getTechnicalIndicators() != null) {
            dataObject.setTechnicalIndicatorsJson(JSON.toJSONString(entity.getTechnicalIndicators()));
        }
        if (entity.getRiskWarnings() != null) {
            dataObject.setRiskWarningsJson(JSON.toJSONString(entity.getRiskWarnings()));
        }
        return dataObject;
    }
}
