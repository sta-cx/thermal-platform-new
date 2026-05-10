package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.domain.vo.PrOptionsHeatVo;

/**
 * 供热系统选项 Mapper
 */
@OrgPermission
public interface PrOptionsHeatMapper extends BaseMapperPlus<PrOptionsHeat, PrOptionsHeatVo> {

    /**
     * 根据公司ID查询公司级别的配置
     * @return 配置信息
     */
    PrOptionsHeat selectByCompanyId();

    /**
     * 根据小区ID和公司ID查询配置
     * @param orgId 小区ID
     * @return 配置信息
     */
    PrOptionsHeat selectByOrgAndCompany(@Param("orgId") String orgId);

    /**
     * 删除指定小区和公司的配置
     * @param orgId 小区ID
     * @return 删除结果
     */
    int deleteByOrgAndCompany(@Param("orgId") String orgId);
}
