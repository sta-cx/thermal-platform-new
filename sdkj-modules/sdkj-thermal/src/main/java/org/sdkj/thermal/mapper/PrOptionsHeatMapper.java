package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrOptionsHeat;
import org.sdkj.thermal.domain.vo.PrOptionsHeatVo;

/**
 * 供热系统选项 Mapper
 */
public interface PrOptionsHeatMapper extends BaseMapperPlus<PrOptionsHeat, PrOptionsHeatVo> {

    /**
     * 根据公司ID查询公司级别的配置
     * @param companyId 公司ID
     * @return 配置信息
     */
    PrOptionsHeat selectByCompanyId(@Param("companyId") String companyId);

    /**
     * 根据小区ID和公司ID查询配置
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 配置信息
     */
    PrOptionsHeat selectByOrgAndCompany(@Param("orgId") String orgId, @Param("companyId") String companyId);

    /**
     * 删除指定小区和公司的配置
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 删除结果
     */
    int deleteByOrgAndCompany(@Param("orgId") String orgId, @Param("companyId") String companyId);
}
