package com.lianghua.domain.analysis.gateway;

import com.lianghua.domain.analysis.model.entity.AnalysisReport;
import com.lianghua.domain.analysis.model.vo.AIProvider;

import java.util.List;
import java.util.Optional;

/**
 * 分析报告网关接口（领域层定义）
 */
public interface AnalysisReportGateway {

    /**
     * 根据ID查询报告
     */
    Optional<AnalysisReport> findById(String reportId);

    /**
     * 根据股票代码查询最新报告
     */
    Optional<AnalysisReport> findLatestBySymbol(String symbol, AIProvider aiProvider);

    /**
     * 查询报告列表
     */
    List<AnalysisReport> findList(String symbol, String aiProvider, String recommendation,
                                   int pageNum, int pageSize);

    /**
     * 保存报告
     */
    void save(AnalysisReport report);
}
