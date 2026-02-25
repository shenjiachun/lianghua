package com.lianghua.app.query;

import com.alibaba.cola.dto.MultiResponse;
import com.lianghua.client.dto.AnalysisResultDTO;
import com.lianghua.client.query.AnalysisResultQry;
import com.lianghua.domain.analysis.gateway.AnalysisResultGateway;
import com.lianghua.domain.analysis.model.entity.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnalysisResultQryExe {

    private final AnalysisResultGateway analysisResultGateway;

    public MultiResponse<AnalysisResultDTO> execute(AnalysisResultQry qry) {
        List<AnalysisResult> results = analysisResultGateway.findByStockCodeAndType(
                qry.getStockCode(), qry.getAnalysisType());
        List<AnalysisResultDTO> dtos = results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtos);
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
