package org.sdkj.thermal.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树形节点 VO
 */
@Data
public class TreeNode implements Serializable {

    private String id;
    private String label;
    private String parentId;
    private List<TreeNode> children = new ArrayList<>();

    public TreeNode() {
    }
}
