package com.lianghua.client.query;

import com.alibaba.cola.dto.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnalysisResultQry extends Query {
    private String stockCode;
    private String analysisType;
    private String aiModel;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNo = 1;
    private Integer pageSize = 20;
}
