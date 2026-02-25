package com.lianghua.client.api;

import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.command.market.AnalysisReportGenerateCmd;
import com.lianghua.client.dto.analysis.AnalysisReportDTO;

/**
 * 量化分析命令服务接口
 */
public interface IAnalysisReportCmdService {

    /**
     * 生成量化分析报告（调用AI大模型进行分析）
     */
    SingleResponse<AnalysisReportDTO> generateReport(AnalysisReportGenerateCmd cmd);
}
