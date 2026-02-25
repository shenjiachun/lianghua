package com.lianghua.client.command.market;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生成量化分析报告命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisReportGenerateCmd extends Command {

    /** 股票代码 */
    private String symbol;

    /**
     * AI模型提供商（ALIYUN/BYTEDANCE）
     * ALIYUN - 阿里云通义千问
     * BYTEDANCE - 字节跳动豆包
     */
    private String aiProvider;

    /** 分析周期（天数），默认30天 */
    private Integer analysisDays;
}
