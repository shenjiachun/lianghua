package com.lianghua.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.dto.analysis.AnalysisReportDTO;
import com.lianghua.client.query.analysis.AnalysisReportBySymbolQuery;
import com.lianghua.client.query.analysis.AnalysisReportListQuery;

/**
 * 量化分析报告查询服务接口
 */
public interface IAnalysisReportQueryService {

    /**
     * 根据股票代码获取最新分析报告
     */
    SingleResponse<AnalysisReportDTO> getReportBySymbol(AnalysisReportBySymbolQuery query);

    /**
     * 查询分析报告列表
     */
    MultiResponse<AnalysisReportDTO> listReports(AnalysisReportListQuery query);
}
