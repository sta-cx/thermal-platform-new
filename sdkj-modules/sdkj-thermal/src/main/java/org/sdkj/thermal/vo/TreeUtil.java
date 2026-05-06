package org.sdkj.thermal.vo;

import java.util.Map;
import org.sdkj.thermal.domain.PrCompany;
import org.sdkj.thermal.domain.SysOrganization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树形工具类
 */
public class TreeUtil {

    /**
     * 将 SysOrganization 列表转换为 TreeNode 列表
     */
    public static List<TreeNode> fromSysOrganizationList(List<SysOrganization> orgs) {
        return orgs.stream().map(TreeUtil::fromSysOrganization).collect(Collectors.toList());
    }

    /**
     * 将单个 SysOrganization 转换为 TreeNode
     */
    public static TreeNode fromSysOrganization(SysOrganization org) {
        TreeNode node = new TreeNode();
        node.setId(org.getId());
        node.setLabel(org.getName());
        node.setParentId(org.getParentId());
        return node;
    }

    /**
     * 将 PrCompany 列表转换为 TreeNode 列表
     */
    public static List<TreeNode> fromPrCompanyList(List<PrCompany> companies) {
        return companies.stream().map(TreeUtil::fromPrCompany).collect(Collectors.toList());
    }

    /**
     * 将单个 PrCompany 转换为 TreeNode
     */
    public static TreeNode fromPrCompany(PrCompany company) {
        TreeNode node = new TreeNode();
        node.setId(String.valueOf(company.getId()));
        node.setParentId(company.getParentId() != null ? company.getParentId() : "-1");
        node.setLabel(company.getName());
        return node;
    }

    public static List<TreeNode> buildByLoop(List<TreeNode> trees, String parentId) {
        if (trees == null || trees.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, TreeNode> map = new HashMap<>();
        for (TreeNode tree : trees) {
            map.put(tree.getId(), tree);
        }

        List<TreeNode> result = new ArrayList<>();
        for (TreeNode tree : trees) {
            String pid = tree.getParentId();
            if (parentId != null && parentId.equals(pid)) {
                result.add(tree);
            } else {
                TreeNode parent = map.get(pid);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(tree);
                }
            }
        }
        return result;
    }
}
