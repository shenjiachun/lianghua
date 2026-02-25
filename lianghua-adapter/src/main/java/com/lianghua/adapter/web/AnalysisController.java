package com.lianghua.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.lianghua.client.api.AnalysisServiceI;
import com.lianghua.client.command.AnalyzeMarketCmd;
import com.lianghua.client.dto.AnalysisResultDTO;
import com.lianghua.client.query.AnalysisResultQry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisServiceI analysisService;

    @PostMapping("/analyze")
    public SingleResponse<AnalysisResultDTO> analyzeMarket(@RequestBody AnalyzeMarketCmd cmd) {
        return analysisService.analyzeMarket(cmd);
    }

    @GetMapping("/history")
    public MultiResponse<AnalysisResultDTO> getAnalysisHistory(
            @RequestParam String stockCode,
            @RequestParam(required = false) String analysisType,
            @RequestParam(required = false) String aiModel) {
        AnalysisResultQry qry = new AnalysisResultQry();
        qry.setStockCode(stockCode);
        qry.setAnalysisType(analysisType);
        qry.setAiModel(aiModel);
        return analysisService.getAnalysisHistory(qry);
    }
}
