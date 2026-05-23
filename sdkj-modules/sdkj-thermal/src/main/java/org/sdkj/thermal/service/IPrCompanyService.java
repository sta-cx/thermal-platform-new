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
     * 返回当前用户可授权的组织树(剪枝后)
     * 超管/租户管理员返回全量,普通用户返回 pr_data_grant 命中节点 + 必要祖先
     */
    List<SysOrganization> getGrantableOrganizationTree(String companyId, Long currentUserId);

    List<SysOrganization> getAllOrganizations();

    /**
     * 含楼栋的组织机构树
     */
    List<TreeNode> queryBuildingTrees(String companyId);

    /**
     * 基于当前用户可访问小区的含楼栋组织机构树
     */
    List<TreeNode> queryUserBuildingTrees(Long userId);

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
    int deleteAllData(String orgId);

    /**
     * 根据ID查询组织机构
     */
    SysOrganization getOrganizationById(String id);

    /**
     * 新增组织机构
     */
    void addOrganization(SysOrganization org);

    /**
     * 修改组织机构
     */
    void updateOrganization(SysOrganization org);

    /**
     * 删除组织机构（带层级校验）
     */
    void deleteOrganization(String id);

    List<String> getUserOrgIds(Long userId, String companyId);

    String getUserCompanyId(Long userId);

    void saveUserOrg(Long userId, String companyId, List<String> orgIds);

    void clearUserOrg(Long userId);

    void createOrgRootNode(String companyId, String name, String code);

    boolean deleteCompanyWithCascade(String id);
}
