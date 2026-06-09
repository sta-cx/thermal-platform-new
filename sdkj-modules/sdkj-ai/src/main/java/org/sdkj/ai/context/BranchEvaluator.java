package org.sdkj.ai.context;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 条件边评估（确定性，不调 LLM）。支持 condition：always / valveClosed / valveError。
 * valveStatus 真实取值集合由配置传入（见 spec §6.4，禁止硬编码字面量）。
 * 新增条件类型在此扩展 eval 分支即可。
 */
@Component
public class BranchEvaluator {

    /** 返回下一 stepId；无分支命中返回 "END"。 */
    public String next(TaskStep step, List<EntityFact> facts,
                       List<String> closedValues, List<String> errorValues) {
        if (step == null || step.getBranches() == null) return "END";
        for (Branch b : step.getBranches()) {
            if (eval(b.condition(), facts, closedValues, errorValues)) return b.gotoStepId();
        }
        return "END";
    }

    private boolean eval(String condition, List<EntityFact> facts,
                        List<String> closedValues, List<String> errorValues) {
        if ("always".equals(condition)) return true;
        EntityFact vf = latestWithKey(facts, "valveStatus");
        return switch (condition) {
            case "valveClosed" -> vf != null && closedValues.contains(str(vf.keys().get("valveStatus")));
            case "valveError" -> vf != null
                && (errorValues.contains(str(vf.keys().get("valveStatus")))
                    || errorValues.contains(str(vf.keys().get("actualStatus"))));
            default -> false;
        };
    }

    private EntityFact latestWithKey(List<EntityFact> facts, String key) {
        if (facts == null) return null;
        for (int i = facts.size() - 1; i >= 0; i--) {
            if (facts.get(i).keys().containsKey(key)) return facts.get(i);
        }
        return null;
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
