package com.lianghua.client.command;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnalyzeMarketCmd extends Command {
    private String stockCode;
    /** TREND, SENTIMENT, RISK, TECHNICAL, FUNDAMENTAL */
    private String analysisType;
    /** QWEN, QWEN_TURBO, DOUBAO, DOUBAO_LITE */
    private String aiModel;
    /** How many days of data to analyze */
    private Integer lookbackDays;
    /** Any additional context for the AI */
    private String extraContext;
}
