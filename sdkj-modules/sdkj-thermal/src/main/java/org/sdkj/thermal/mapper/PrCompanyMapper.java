package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrCompanyVo;

import java.util.List;

public interface PrCompanyMapper extends BaseMapperPlus<PrCompany, PrCompanyVo> {

    /**
     * 查询公司下所有组织机构（不含部门）
     */
    List<SysOrganization> selectOrganizationsByCompanyId(@Param("companyId") String companyId);

    /**
     * 获取公司根节点（parent_id = '-1'）
     */
    SysOrganization getCompany(@Param("companyId") String companyId);

    /**
     * 根据ID查询组织机构
     */
    SysOrganization selectOrgById(@Param("id") String id);

    /**
     * 获取用户数据权限范围内的组织机构（树形）
     */
    List<SysOrganization> getDataGrantOrg(@Param("companyId") String companyId,
                                          @Param("userId") Long userId);

    /**
     * 获取用户可访问的小区列表
     */
    List<SysOrganization> getUserOrgByUserId(@Param("userId") Long userId);

    /**
     * 获取用户可访问的分公司列表
     */
    List<SysOrganization> getUserOrgBranchByUserId(@Param("userId") Long userId);

    // ========== 新增/修改 ==========

    int insertOrganization(SysOrganization org);

    int updateOrganization(SysOrganization org);

    // ========== 校验方法 ==========

    /**
     * 查询组织机构是否存在子节点
     */
    int findChild(@Param("id") String id, @Param("companyId") String companyId);

    /**
     * 查询小区下是否存在楼宇
     */
    int findBuilding(@Param("id") String id, @Param("companyId") String companyId);

    /**
     * 查询部门下是否存在人员
     */
    int findDeptById(@Param("id") String id, @Param("companyId") String companyId);

    // ========== 级联删除方法 ==========

    int deleteOrgById(@Param("id") String id);

    int deleteGrantDataById(@Param("id") String id, @Param("companyId") String companyId);

    int deleteBuildingData(@Param("orgId") String orgId);

    int deleteHouseData(@Param("orgId") String orgId);

    int deleteUnitData(@Param("orgId") String orgId);

    int deleteUserHouseData(@Param("orgId") String orgId);

    int deleteValveData(@Param("orgId") String orgId);

    int deleteUnitValveData(@Param("orgId") String orgId);

    int deleteHotData(@Param("orgId") String orgId);

    int deleteUnitHotData(@Param("orgId") String orgId);

    int deleteWcqData(@Param("orgId") String orgId);

    int deleteDtuData(@Param("orgId") String orgId);
}
