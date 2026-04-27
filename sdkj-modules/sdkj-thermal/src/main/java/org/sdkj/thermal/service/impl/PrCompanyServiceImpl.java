package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.service.IPrCompanyService;
import org.sdkj.thermal.vo.TreeNode;
import org.sdkj.thermal.vo.TreeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrCompanyServiceImpl extends ServiceImpl<PrCompanyMapper, PrCompany>
        implements IPrCompanyService {

    private final PrCompanyMapper prCompanyMapper;
    private final PrBuildingMapper prBuildingMapper;

    @Override
    public List<PrCompany> listCompanies() {
        return prCompanyMapper.selectList(null);
    }

    @Override
    public List<SysOrganization> getOrganizationsByCompanyId(String companyId) {
        return prCompanyMapper.selectOrganizationsByCompanyId(companyId);
    }

    @Override
    public List<TreeNode> queryBuildingTrees(String companyId) {
        // 获取公司所有组织机构（不含部门）
        List<SysOrganization> orgs = prCompanyMapper.selectOrganizationsByCompanyId(companyId);

        // 收集所有小区ID (level = '2')，用于查询楼栋
        List<String> orgIds = orgs.stream()
                .map(SysOrganization::getId)
                .collect(Collectors.toList());

        // 批量查询楼栋
        List<PrBuilding> buildings = new ArrayList<>();
        if (!orgIds.isEmpty()) {
            buildings = prBuildingMapper.selectList(
                    new LambdaQueryWrapper<PrBuilding>()
                            .eq(PrBuilding::getCompanyId, companyId)
                            .in(PrBuilding::getOrgId, orgIds)
                            .orderByAsc(PrBuilding::getCode)
            );
        }

        // 将组织机构转换为 TreeNode
        List<TreeNode> nodes = orgs.stream().map(o -> {
            TreeNode node = new TreeNode();
            node.setId(o.getId());
            node.setLabel(o.getName());
            node.setParentId(o.getParentId());
            return node;
        }).collect(Collectors.toList());

        // 将楼栋转换为虚拟 TreeNode（作为对应的 org 的子节点）
        for (PrBuilding b : buildings) {
            TreeNode node = new TreeNode();
            node.setId(b.getId());
            node.setLabel(b.getName());
            node.setParentId(b.getOrgId());
            nodes.add(node);
        }

        // 构建树形结构
        return TreeUtil.buildByLoop(nodes, "-1");
    }

    @Override
    public List<TreeNode> getDataGrantOrg(String companyId, Long userId) {
        List<SysOrganization> orgs = prCompanyMapper.getDataGrantOrg(
                companyId, String.valueOf(userId));

        List<TreeNode> nodes = orgs.stream().map(o -> {
            TreeNode node = new TreeNode();
            node.setId(o.getId());
            node.setLabel(o.getName());
            node.setParentId(o.getParentId());
            return node;
        }).collect(Collectors.toList());

        return TreeUtil.buildByLoop(nodes, "-1");
    }

    @Override
    public List<SysOrganization> getUserOrg(Long userId) {
        return prCompanyMapper.getUserOrgByUserId(String.valueOf(userId));
    }

    @Override
    public List<SysOrganization> getUserOrgBranch(Long userId) {
        return prCompanyMapper.getUserOrgBranchByUserId(String.valueOf(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAllData(Long orgId) {
        SysOrganization org = prCompanyMapper.selectOrgById(String.valueOf(orgId));
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
            // 小区：级联删除所有关联数据
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
}
