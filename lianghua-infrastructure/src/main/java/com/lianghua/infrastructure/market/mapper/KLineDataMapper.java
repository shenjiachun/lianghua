package com.lianghua.infrastructure.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianghua.infrastructure.market.dataobject.KLineDataDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * K线数据Mapper
 */
@Mapper
public interface KLineDataMapper extends BaseMapper<KLineDataDO> {

    @Select("SELECT * FROM kline_data WHERE symbol = #{symbol} ORDER BY trade_date DESC LIMIT #{days}")
    List<KLineDataDO> findRecentBySymbol(@Param("symbol") String symbol, @Param("days") int days);
}
