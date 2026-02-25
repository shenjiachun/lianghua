package com.lianghua.infrastructure.persistence.mapper;

import com.lianghua.infrastructure.persistence.do_.MarketDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MarketDataMapper {

    void insert(MarketDataDO marketDataDO);

    void insertBatch(@Param("list") List<MarketDataDO> list);

    MarketDataDO selectById(@Param("id") String id);

    List<MarketDataDO> selectByStockCode(@Param("stockCode") String stockCode,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    MarketDataDO selectLatestByStockCode(@Param("stockCode") String stockCode);
}
