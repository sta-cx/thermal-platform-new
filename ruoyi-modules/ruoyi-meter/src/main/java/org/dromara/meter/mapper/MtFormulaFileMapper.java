package org.dromara.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.meter.domain.MtFormulaFile;
import org.dromara.meter.domain.vo.MtFormulaFileVo;

import java.util.List;
import java.util.Map;

/**
 * 公式档案Mapper
 */
public interface MtFormulaFileMapper extends BaseMapperPlus<MtFormulaFile, MtFormulaFileVo> {

    /** 校验名称是否重复 */
    int validateName(@Param("name") String name, @Param("id") String id);

    /** 获取公式类型列表（从字典表） */
    List<Map<String, Object>> getFormulaType();

    /** 获取公式元素列表（从字典表） */
    List<Map<String, Object>> getFormulaElement();

    /** 根据类型查询启用的公式 */
    List<MtFormulaFile> selectByType(@Param("type") String type);
}
