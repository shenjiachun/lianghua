package com.lianghua.app.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.app.command.AnalyzeMarketCmdExe;
import com.lianghua.app.query.AnalysisResultQryExe;
import com.lianghua.client.api.AnalysisServiceI;
import com.lianghua.client.command.AnalyzeMarketCmd;
import com.lianghua.client.dto.AnalysisResultDTO;
import com.lianghua.client.query.AnalysisResultQry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisServiceI {

    private final AnalyzeMarketCmdExe analyzeMarketCmdExe;
    private final AnalysisResultQryExe analysisResultQryExe;

    @Override
    public SingleResponse<AnalysisResultDTO> analyzeMarket(AnalyzeMarketCmd cmd) {
        return analyzeMarketCmdExe.execute(cmd);
    }

    @Override
    public MultiResponse<AnalysisResultDTO> getAnalysisHistory(AnalysisResultQry qry) {
        return analysisResultQryExe.execute(qry);
    }
}
