package com.lianghua.adapter.analysis;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.api.IAnalysisReportCmdService;
import com.lianghua.client.api.IAnalysisReportQueryService;
import com.lianghua.client.command.market.AnalysisReportGenerateCmd;
import com.lianghua.client.dto.analysis.AnalysisReportDTO;
import com.lianghua.client.query.analysis.AnalysisReportBySymbolQuery;
import com.lianghua.client.query.analysis.AnalysisReportListQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 量化分析报告REST接口
 */
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisReportController {

    private final IAnalysisReportQueryService analysisReportQueryService;
    private final IAnalysisReportCmdService analysisReportCmdService;

    /**
     * 根据股票代码获取最新分析报告
     */
    @GetMapping("/report/{symbol}")
    public SingleResponse<AnalysisReportDTO> getReportBySymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) String aiProvider) {
        AnalysisReportBySymbolQuery query = new AnalysisReportBySymbolQuery();
        query.setSymbol(symbol);
        query.setAiProvider(aiProvider);
        return analysisReportQueryService.getReportBySymbol(query);
    }

    /**
     * 查询分析报告列表
     */
    @GetMapping("/report/list")
    public MultiResponse<AnalysisReportDTO> listReports(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String aiProvider,
            @RequestParam(required = false) String recommendation,
            @RequestParam(defaultValue = "1") int pageIndex,
            @RequestParam(defaultValue = "20") int pageSize) {
        AnalysisReportListQuery query = new AnalysisReportListQuery();
        query.setSymbol(symbol);
        query.setAiProvider(aiProvider);
        query.setRecommendation(recommendation);
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        return analysisReportQueryService.listReports(query);
    }

    /**
     * 生成量化分析报告（调用AI大模型）
     */
    @PostMapping("/report/generate")
    public SingleResponse<AnalysisReportDTO> generateReport(
            @RequestBody AnalysisReportGenerateCmd cmd) {
        return analysisReportCmdService.generateReport(cmd);
    }
}
