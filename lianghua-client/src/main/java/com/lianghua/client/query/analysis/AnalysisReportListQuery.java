package com.lianghua.client.query.analysis;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析报告列表查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisReportListQuery extends PageQuery {

    /** 股票代码 */
    private String symbol;

    /** AI提供商（ALIYUN/BYTEDANCE） */
    private String aiProvider;

    /** 投资建议过滤（BUY/HOLD/SELL） */
    private String recommendation;
}
