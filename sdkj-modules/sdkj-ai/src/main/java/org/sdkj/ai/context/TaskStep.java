package org.sdkj.ai.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStep {
    private String stepId;
    private String toolName;              // 方法名，如 getByHouseId / dispatch / create
    private String desc;                  // 给用户看的步骤描述
    private Map<String, Object> presetArgs;
    private List<Branch> branches;        // 按顺序匹配，首个命中的决定下一步
    private String stepStatus;            // WAITING / COMPLETED / SKIPPED
}
