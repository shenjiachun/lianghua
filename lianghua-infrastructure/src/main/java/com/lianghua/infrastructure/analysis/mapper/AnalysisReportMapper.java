package com.lianghua.infrastructure.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianghua.infrastructure.analysis.dataobject.AnalysisReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分析报告Mapper
 */
@Mapper
public interface AnalysisReportMapper extends BaseMapper<AnalysisReportDO> {
}
