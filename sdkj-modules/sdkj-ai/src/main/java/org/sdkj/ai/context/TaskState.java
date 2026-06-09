package org.sdkj.ai.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskState {
    private String taskId;
    private String taskType;
    private String title;
    private List<TaskStep> steps;
    private String currentStepId;         // null 或 "END" 表示无后续
    private String status;                // AWAITING_APPROVAL / RUNNING / AWAITING_CONFIRM / DONE / ABORTED
    private String pendingCallId;         // 当前等待确认的写步 callId

    public TaskStep stepById(String id) {
        if (steps == null || id == null) return null;
        return steps.stream().filter(s -> id.equals(s.getStepId())).findFirst().orElse(null);
    }
}
