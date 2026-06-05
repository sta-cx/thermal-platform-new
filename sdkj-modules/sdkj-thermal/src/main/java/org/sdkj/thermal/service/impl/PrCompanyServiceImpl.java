package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.PrDataGrant;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
import org.sdkj.thermal.service.IPrCompanyService;
import org.sdkj.thermal.service.OrgAccessService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrCompanyServiceImpl extends ServiceImpl<PrCompanyMapper, PrCompany>
        implements IPrCompanyService {

    private final PrCompanyMapper prCompanyMapper;
    private final PrBuildingMapper prBuildingMapper;
    private final PrDataGrantMapper prDataGrantMapper;
    private final OrgAccessService orgAccessService;

    @Override
    public List<PrCompany> listCompanies() {
        return prCompanyMapper.selectList(null);
    }

    @Override
    public List<SysOrganization> getOrganizationsByCompanyId(String companyId) {
        return prCompanyMapper.selectOrganizationsByCompanyId(companyId);
    }

    @Override
    public List<SysOrganization> getGrantableOrganizationTree(String companyId, Long currentUserId) {
        List<SysOrganization> allOrgs = prCompanyMapper.selectOrganizationsByCompanyId(companyId);
        Set<String> grantableIds = getGrantableOrgIdsForUser(companyId, currentUserId);

        if (grantableIds == null) {
            return buildOrgTreeFlat(allOrgs, "-1");
        }
        if (grantableIds.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> keepIds = new java.util.HashSet<>(grantableIds);
        java.util.Map<String, SysOrganization> byId = new java.util.HashMap<>();
        for (SysOrganization o : allOrgs) {
            byId.put(o.getId(), o);
        }
        for (String orgId : grantableIds) {
            String cursor = orgId;
            while (cursor != null) {
                SysOrganization node = byId.get(cursor);
                if (node == null) break;
                keepIds.add(cursor);
                cursor = node.getParentId();
                if (cursor == null || "-1".equals(cursor) || "0".equals(cursor)) break;
            }
        }

        List<SysOrganization> filtered = allOrgs.stream()
            .filter(o -> keepIds.contains(o.getId()))
            .collect(Collectors.toList());

        return buildOrgTreeFlat(filtered, "-1");
    }

    private List<SysOrganization> buildOrgTreeFlat(List<SysOrganization> all, String parentId) {
        return all.stream()
            .filter(o -> parentId.equals(o.getParentId()))
            .peek(o -> o.setChildren(buildOrgTreeFlat(all, o.getId())))
            .collect(Collectors.toList());
    }

    @Override
    public List<SysOrganization> getAllOrganizations() {
        return prCompanyMapper.selectAllOrganizations();
    }

    @Override
    public List<TreeNode> queryBuildingTrees(String companyId) {
        List<SysOrganization> orgs = prCompanyMapper.selectOrganizationsByCompanyId(companyId);
        return buildBuildingTree(orgs);
    }

    @Override
    public List<TreeNode> queryUserBuildingTrees(Long userId) {
        List<SysOrganization> orgs = getUserOrg(userId);
        return buildBuildingTree(orgs);
    }

    private List<TreeNode> buildBuildingTree(List<SysOrganization> orgs) {
        List<String> orgIds = orgs.stream()
                .map(SysOrganization::getId)
                .collect(Collectors.toList());

        List<PrBuilding> buildings = new ArrayList<>();
        if (!orgIds.isEmpty()) {
            buildings = prBuildingMapper.selectList(
                    new LambdaQueryWrapper<PrBuilding>()
                            .in(PrBuilding::getOrgId, orgIds)
                            .orderByAsc(PrBuilding::getCode)
            );
        }

        List<TreeNode> nodes = TreeUtil.fromSysOrganizationList(orgs);
        for (PrBuilding b : buildings) {
            TreeNode node = new TreeNode();
            node.setId(String.valueOf(b.getId()));
            node.setLabel(b.getName());
            node.setParentId(b.getOrgId());
            nodes.add(node);
        }

        return TreeUtil.buildByLoop(nodes, "-1");
    }

    @Override
    public List<TreeNode> getDataGrantOrg(String companyId, Long userId) {
        List<SysOrganization> orgs = prCompanyMapper.getDataGrantOrg(
                companyId, userId);

        return TreeUtil.buildByLoop(TreeUtil.fromSysOrganizationList(orgs), "-1");
    }

    @Override
    public List<SysOrganization> getUserOrg(Long userId) {
        if (LoginHelper.isSuperAdmin(userId) || LoginHelper.isTenantAdmin()) {
            return prCompanyMapper.listAllOrgsByLevel("2");
        }
        return prCompanyMapper.getUserOrgByUserId(userId);
    }

    @Override
    public List<SysOrganization> getUserOrgBranch(Long userId) {
        return prCompanyMapper.getUserOrgBranchByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAllData(String orgId) {
        SysOrganization org = prCompanyMapper.selectOrgById(orgId);
        if (org == null) {
            return 0;
        }

        String orgIdStr = org.getId();
        String companyId = org.getCompanyId();
        String level = org.getLevel();

        if ("0".equals(level)) {
            // 总公司不可删除
            log.warn("总公司不可删除, orgId={}", orgId);
            return 0;
        }

        if ("1".equals(level)) {
            // 分公司：检查有无下级
            int childCount = prCompanyMapper.findChild(orgIdStr, companyId);
            if (childCount > 0) {
                log.warn("分公司存在下级,不可删除, orgId={}", orgId);
                return 0;
            }
            prCompanyMapper.deleteOrgById(orgIdStr);
            return 1;
        }

        if ("2".equals(level)) {
            orgAccessService.assertCurrentUserCanAccessOrg(orgIdStr);
            // 小区：检查有无子部门，有则拒绝删除
            int childCount = prCompanyMapper.findChild(orgIdStr, companyId);
            if (childCount > 0) {
                log.warn("小区下存在部门,不可删除, orgId={}", orgId);
                return 0;
            }
            // 级联删除所有关联数据
            prCompanyMapper.deleteValveData(orgIdStr);
            prCompanyMapper.deleteUnitValveData(orgIdStr);
            prCompanyMapper.deleteHotData(orgIdStr);
            prCompanyMapper.deleteUnitHotData(orgIdStr);
            prCompanyMapper.deleteWcqData(orgIdStr);
            prCompanyMapper.deleteDtuData(orgIdStr);
            prCompanyMapper.deleteHouseData(orgIdStr);
            prCompanyMapper.deleteUserHouseData(orgIdStr);
            prCompanyMapper.deleteUnitData(orgIdStr);
            prCompanyMapper.deleteBuildingData(orgIdStr);
            prCompanyMapper.deleteGrantDataById(orgIdStr, companyId);
            prCompanyMapper.deleteOrgById(orgIdStr);
            return 1;
        }

        if ("3".equals(level)) {
            // 部门：检查有无人员
            int userCount = prCompanyMapper.findDeptById(orgIdStr, companyId);
            if (userCount > 0) {
                log.warn("部门下存在人员,不可删除, orgId={}", orgId);
                return 0;
            }
            prCompanyMapper.deleteOrgById(orgIdStr);
            return 1;
        }

        return 0;
    }

    @Override
    public List<String> getUserOrgIds(Long userId, String companyId) {
        List<PrDataGrant> grants = prDataGrantMapper.selectList(
            new LambdaQueryWrapper<PrDataGrant>()
                .eq(PrDataGrant::getUserId, userId)
                .eq(PrDataGrant::getCompanyId, companyId)
        );
        return grants.stream().map(PrDataGrant::getOrgId).collect(Collectors.toList());
    }

    @Override
    public String getUserCompanyId(Long userId) {
        List<PrDataGrant> grants = prDataGrantMapper.selectList(
            new LambdaQueryWrapper<PrDataGrant>()
                .eq(PrDataGrant::getUserId, userId)
                .last("LIMIT 1")
        );
        return grants.isEmpty() ? null : grants.get(0).getCompanyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserOrg(Long userId, String companyId, List<String> orgIds) {
        Long currentUserId = LoginHelper.getUserId();
        boolean isPrivileged = LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin();

        // 服务端校验:普通用户只能授予自己拥有的 org
        if (!isPrivileged && orgIds != null && !orgIds.isEmpty()) {
            Set<String> grantable = getGrantableOrgIdsForUser(companyId, currentUserId);
            for (String orgId : orgIds) {
                if (grantable == null || !grantable.contains(orgId)) {
                    throw new ServiceException("无权授予组织: " + orgId);
                }
            }
        }

        // 双条件 DELETE:
        //   始终按 company_id 过滤(不影响该用户在其他公司的授权)
        //   普通用户额外按 create_by 过滤(只撤自己创建的,不动他人授权)
        LambdaQueryWrapper<PrDataGrant> deleteWrapper = new LambdaQueryWrapper<PrDataGrant>()
            .eq(PrDataGrant::getUserId, userId)
            .eq(PrDataGrant::getCompanyId, companyId);
        if (!isPrivileged) {
            deleteWrapper.eq(PrDataGrant::getCreateBy, currentUserId);
        }
        prDataGrantMapper.delete(deleteWrapper);

        // 重新插入新选中的 orgIds
        if (orgIds != null && !orgIds.isEmpty()) {
            List<PrDataGrant> grants = orgIds.stream().map(orgId -> {
                PrDataGrant g = new PrDataGrant();
                g.setUserId(userId);
                g.setCompanyId(companyId);
                g.setOrgId(orgId);
                return g;
            }).collect(Collectors.toList());
            prDataGrantMapper.insertBatch(grants);
        }
    }

    @Override
    public void clearUserOrg(Long userId) {
        Long currentUserId = LoginHelper.getUserId();
        boolean isPrivileged = LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin();
        LambdaQueryWrapper<PrDataGrant> deleteWrapper = new LambdaQueryWrapper<PrDataGrant>()
            .eq(PrDataGrant::getUserId, userId);
        if (!isPrivileged) {
            deleteWrapper.eq(PrDataGrant::getCreateBy, currentUserId);
        }
        prDataGrantMapper.delete(deleteWrapper);
    }

    @Override
    public SysOrganization getOrganizationById(String id) {
        return prCompanyMapper.selectOrgById(id);
    }

    @Override
    public void addOrganization(SysOrganization org) {
        if (org.getId() == null) {
            org.setId(IdWorker.getIdStr(org));
        }
        prCompanyMapper.insertOrganization(org);
    }

    @Override
    public void updateOrganization(SysOrganization org) {
        prCompanyMapper.updateOrganization(org);
    }

    @Override
    public void deleteOrganization(String id) {
        SysOrganization org = prCompanyMapper.selectOrgById(id);
        if (org == null) {
            throw new ServiceException("组织机构不存在");
        }
        if ("0".equals(org.getLevel())) {
            throw new ServiceException("总公司不可删除");
        }
        int childCount = prCompanyMapper.findChild(id, org.getCompanyId());
        if (childCount > 0) {
            throw new ServiceException("存在下级节点，不可删除");
        }
        prCompanyMapper.deleteOrgById(id);
        prCompanyMapper.deleteGrantDataById(id, org.getCompanyId());
    }

    @Override
    public void createOrgRootNode(String companyId, String name, String code) {
        SysOrganization root = new SysOrganization();
        root.setId(IdWorker.getIdStr(root));
        root.setName(name);
        root.setCode(code);
        root.setCompanyId(companyId);
        root.setParentId("-1");
        root.setLevel("0");
        prCompanyMapper.insertOrganization(root);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCompanyWithCascade(String id) {
        // 级联删除数据权限
        prDataGrantMapper.delete(
            new LambdaQueryWrapper<PrDataGrant>().eq(PrDataGrant::getCompanyId, id)
        );
        // 级联删除组织机构（含根节点 level=0，因为查询条件是 level != '3'）
        List<SysOrganization> orgs = prCompanyMapper.selectOrganizationsByCompanyId(id);
        for (SysOrganization org : orgs) {
            prCompanyMapper.deleteOrgById(org.getId());
        }
        return removeById(id);
    }

    /**
     * 返回当前用户在指定公司可授权的 org_id 集合
     * 超管/租户管理员返回 null 表示"不受限"
     */
    private Set<String> getGrantableOrgIdsForUser(String companyId, Long currentUserId) {
        if (LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin()) {
            return null;
        }
        List<PrDataGrant> grants = prDataGrantMapper.selectList(
            new LambdaQueryWrapper<PrDataGrant>()
                .eq(PrDataGrant::getUserId, currentUserId)
                .eq(PrDataGrant::getCompanyId, companyId)
        );
        return grants.stream()
            .map(PrDataGrant::getOrgId)
            .collect(Collectors.toSet());
    }
}
