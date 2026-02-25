package com.lianghua.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.command.AnalyzeMarketCmd;
import com.lianghua.client.dto.AnalysisResultDTO;
import com.lianghua.client.query.AnalysisResultQry;

public interface AnalysisServiceI {

    SingleResponse<AnalysisResultDTO> analyzeMarket(AnalyzeMarketCmd cmd);

    MultiResponse<AnalysisResultDTO> getAnalysisHistory(AnalysisResultQry qry);
}
