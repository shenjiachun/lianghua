package com.lianghua.infrastructure.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianghua.infrastructure.market.dataobject.MarketDataDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 市场数据Mapper
 */
@Mapper
public interface MarketDataMapper extends BaseMapper<MarketDataDO> {
}
