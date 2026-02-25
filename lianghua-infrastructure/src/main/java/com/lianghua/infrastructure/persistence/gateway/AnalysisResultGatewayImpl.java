package com.lianghua.infrastructure.persistence.gateway;

import com.lianghua.domain.analysis.gateway.AnalysisResultGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisResult;
import com.lianghua.domain.analysis.model.valobj.AIModel;
import com.lianghua.domain.analysis.model.valobj.AnalysisType;
import com.lianghua.domain.analysis.model.valobj.TradingSuggestion;
import com.lianghua.infrastructure.persistence.do_.AnalysisResultDO;
import com.lianghua.infrastructure.persistence.mapper.AnalysisResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnalysisResultGatewayImpl implements AnalysisResultGateway {

    private final AnalysisResultMapper analysisResultMapper;

    @Override
    public void save(AnalysisResult result) {
        analysisResultMapper.insert(toDataObject(result));
    }

    @Override
    public Optional<AnalysisResult> findById(String id) {
        AnalysisResultDO dataObject = analysisResultMapper.selectById(id);
        return Optional.ofNullable(dataObject).map(this::toDomain);
    }

    @Override
    public List<AnalysisResult> findByStockCode(String stockCode, LocalDate startDate, LocalDate endDate) {
        List<AnalysisResultDO> doList = analysisResultMapper.selectByStockCode(stockCode, startDate, endDate);
        return doList.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AnalysisResult> findByStockCodeAndType(String stockCode, String analysisType) {
        List<AnalysisResultDO> doList = analysisResultMapper.selectByStockCodeAndType(stockCode, analysisType);
        return doList.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private AnalysisResultDO toDataObject(AnalysisResult domain) {
        AnalysisResultDO dataObject = new AnalysisResultDO();
        dataObject.setId(domain.getId());
        dataObject.setStockCode(domain.getStockCode().getFullCode());
        dataObject.setAnalysisType(domain.getAnalysisType().name());
        dataObject.setAiModel(domain.getAiModel().name());
        dataObject.setPrompt(domain.getPrompt());
        dataObject.setContent(domain.getContent());
        dataObject.setSuggestion(domain.getSuggestion() != null ? domain.getSuggestion().name() : null);
        dataObject.setConfidenceScore(domain.getConfidenceScore());
        dataObject.setCreatedAt(domain.getCreatedAt());
        return dataObject;
    }

    private AnalysisResult toDomain(AnalysisResultDO dataObject) {
        TradingSuggestion suggestion = null;
        if (dataObject.getSuggestion() != null) {
            try {
                suggestion = TradingSuggestion.valueOf(dataObject.getSuggestion());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return AnalysisResult.reconstruct(
                dataObject.getId(),
                dataObject.getStockCode(),
                AnalysisType.valueOf(dataObject.getAnalysisType()),
                AIModel.valueOf(dataObject.getAiModel()),
                dataObject.getPrompt(),
                dataObject.getContent(),
                suggestion,
                dataObject.getConfidenceScore(),
                dataObject.getCreatedAt()
        );
    }
}
