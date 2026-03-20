package com.lianghua.client.query.market;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 市场数据列表查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketDataListQuery extends PageQuery {

    /** 市场类型，如 SH/SZ/US */
    private String market;

    /** 行业 */
    private String industry;

    /** 是否只看涨停 */
    private Boolean limitUp;
}
