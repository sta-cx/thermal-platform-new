package org.sdkj.thermal.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形工具类
 */
public class TreeUtil {

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
            if (parentId.equals(pid)) {
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
