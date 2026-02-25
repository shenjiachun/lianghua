package com.lianghua.domain.analysis.gateway;

import com.lianghua.domain.analysis.model.entity.AnalysisResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalysisResultGateway {

    void save(AnalysisResult result);

    Optional<AnalysisResult> findById(String id);

    List<AnalysisResult> findByStockCode(String stockCode, LocalDate startDate, LocalDate endDate);

    List<AnalysisResult> findByStockCodeAndType(String stockCode, String analysisType);
}
