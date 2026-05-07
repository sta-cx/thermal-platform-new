package org.sdkj.thermal.vo;

import org.sdkj.thermal.domain.SysOrganization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 树形工具类
 */
public class TreeUtil {

    public static List<TreeNode> fromSysOrganizationList(List<SysOrganization> orgs) {
        return orgs.stream().map(TreeUtil::fromSysOrganization).collect(Collectors.toList());
    }

    public static TreeNode fromSysOrganization(SysOrganization org) {
        TreeNode node = new TreeNode();
        node.setId(org.getId());
        node.setLabel(org.getName());
        node.setParentId(org.getParentId());
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
