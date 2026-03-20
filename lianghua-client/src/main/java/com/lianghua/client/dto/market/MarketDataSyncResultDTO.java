package com.lianghua.client.dto.market;

import lombok.Data;

/**
 * 市场数据同步结果DTO
 */
@Data
public class MarketDataSyncResultDTO {

    /** 同步股票代码 */
    private String symbol;

    /** 是否成功 */
    private boolean success;

    /** 同步条数 */
    private int syncCount;

    /** 消息 */
    private String message;
}
