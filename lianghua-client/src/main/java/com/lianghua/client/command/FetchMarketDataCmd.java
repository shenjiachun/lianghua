package com.lianghua.client.command;

import com.alibaba.cola.dto.Command;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class FetchMarketDataCmd extends Command {
    /** e.g. "000001.SZ" */
    private String stockCode;
    private LocalDate startDate;
    private LocalDate endDate;
    /** TUSHARE, SINA, MANUAL */
    private String dataSource;
}
