package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;

/**
 * 热力DTU档案 Mapper
 */
public interface PrHeatDtuArchiveMapper extends BaseMapperPlus<PrHeatDtuArchive, PrHeatDtuArchiveVo> {

    /**
     * 生成阀门查询指令
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param dtuNum DTU编号
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean setTasksPerformValve(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                  @Param("dtuNum") String dtuNum, @Param("createBy") String createBy);

    /**
     * 生成单元阀门查询指令
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param dtuNum DTU编号
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean setTasksPerformUnitValve(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                      @Param("dtuNum") String dtuNum, @Param("createBy") String createBy);

    /**
     * 生成热表查询指令
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param dtuNum DTU编号
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean setTasksPerformHot(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                @Param("dtuNum") String dtuNum, @Param("createBy") String createBy);

    /**
     * 生成单元热表查询指令
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param dtuNum DTU编号
     * @param createBy 创建人
     * @return 是否成功
     */
    boolean setTasksPerformUnitHot(@Param("companyId") String companyId, @Param("orgId") String orgId,
                                    @Param("dtuNum") String dtuNum, @Param("createBy") String createBy);
}
