package com.lianghua.client.query.analysis;

import com.alibaba.cola.dto.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据股票代码查询分析报告
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisReportBySymbolQuery extends Query {

    /** 股票代码 */
    private String symbol;

    /** AI提供商（ALIYUN/BYTEDANCE），为空则返回最新的 */
    private String aiProvider;
}
