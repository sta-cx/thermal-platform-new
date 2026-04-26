package org.sdkj.thermal.vo;

import lombok.Data;
import org.sdkj.thermal.domain.AgCompany;

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

    public TreeNode(AgCompany company) {
        this.id = String.valueOf(company.getId());
        this.label = company.getName();
        this.parentId = String.valueOf(company.getParentId());
    }
}
