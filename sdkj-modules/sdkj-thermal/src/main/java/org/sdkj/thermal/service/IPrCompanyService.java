package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.vo.TreeNode;

import java.util.List;

public interface IPrCompanyService extends IService<PrCompany> {

    List<PrCompany> listCompanies();

    List<SysOrganization> getOrganizationsByCompanyId(String companyId);

    /**
     * 含楼栋的组织机构树
     */
    List<TreeNode> queryBuildingTrees(String companyId);

    /**
     * 获取用户数据权限树
     */
    List<TreeNode> getDataGrantOrg(String companyId, Long userId);

    /**
     * 获取当前用户可访问的小区列表
     */
    List<SysOrganization> getUserOrg(Long userId);

    /**
     * 获取用户所属分公司列表
     */
    List<SysOrganization> getUserOrgBranch(Long userId);

    /**
     * 级联删除组织机构及其子节点
     */
    int deleteAllData(Long orgId);
}
