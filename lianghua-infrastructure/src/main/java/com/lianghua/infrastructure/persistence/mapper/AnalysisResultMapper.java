package com.lianghua.infrastructure.persistence.mapper;

import com.lianghua.infrastructure.persistence.do_.AnalysisResultDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AnalysisResultMapper {

    void insert(AnalysisResultDO analysisResultDO);

    AnalysisResultDO selectById(@Param("id") String id);

    List<AnalysisResultDO> selectByStockCode(@Param("stockCode") String stockCode,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    List<AnalysisResultDO> selectByStockCodeAndType(@Param("stockCode") String stockCode,
                                                     @Param("analysisType") String analysisType);
}
