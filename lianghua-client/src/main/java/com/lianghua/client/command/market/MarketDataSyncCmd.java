package com.lianghua.client.command.market;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 同步市场数据命令
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketDataSyncCmd extends Command {

    /** 股票代码 */
    private String symbol;

    /** 数据来源（TUSHARE/EASTMONEY/SINA） */
    private String dataSource;
}
