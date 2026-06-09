package org.sdkj.ai.context;

import java.util.ArrayList;
import java.util.List;

/**
 * 编排终态「一句收尾」（spec §6.5）。确定性、纯函数、可单测，不调 LLM。
 * - 供热投诉模板：依据阀门 fact 给结构化结论（开/关/故障/无档案）。
 * - 通用 LLM 计划：复述已完成步骤（stepStatus==COMPLETED）。
 * - 中止：取消/异常说明。
 */
public final class OrchestrationSummary {

    private OrchestrationSummary() {}

    public static String build(TaskState ts, EntityFact valveFact, String status, String reason,
                               List<String> closedValues, List<String> errorValues) {
        String title = (ts == null || ts.getTitle() == null || ts.getTitle().isBlank()) ? "任务" : ts.getTitle();

        if ("ABORTED".equals(status)) {
            return (reason == null || reason.isBlank())
                ? "已取消计划「" + title + "」，未继续执行。"
                : "计划「" + title + "」已中止：" + reason + "。";
        }

        // DONE：供热投诉模板给富文本结论
        if (ts != null && "handle_heating_complaint".equals(ts.getTaskType())) {
            return heating(valveFact, closedValues, errorValues);
        }

        // DONE：通用计划复述已完成步骤
        List<String> done = new ArrayList<>();
        if (ts != null && ts.getSteps() != null) {
            for (TaskStep s : ts.getSteps()) {
                if ("COMPLETED".equals(s.getStepStatus()) && s.getDesc() != null && !s.getDesc().isBlank()) {
                    done.add(s.getDesc());
                }
            }
        }
        if (done.isEmpty()) return "计划「" + title + "」已结束。";
        StringBuilder sb = new StringBuilder("计划「").append(title).append("」执行完成。已完成：");
        for (int i = 0; i < done.size(); i++) {
            if (i > 0) sb.append("；");
            sb.append(i + 1).append(") ").append(done.get(i));
        }
        sb.append("。");
        return sb.toString();
    }

    private static String heating(EntityFact vf, List<String> closedValues, List<String> errorValues) {
        String house = (vf != null && vf.keys() != null && vf.keys().get("houseId") != null)
            ? "房屋" + vf.keys().get("houseId") : "该户";
        if (vf == null || vf.keys() == null) {
            return "未查询到" + house + "的阀门档案，无法判断供热状态，已结束本次处理。";
        }
        String vs = str(vf.keys().get("valveStatus"));
        String as = str(vf.keys().get("actualStatus"));
        if (contains(closedValues, vs)) {
            return "已查询" + house + "阀门：此前为「关闭」，已按计划执行开阀操作。任务完成。";
        }
        if (contains(errorValues, vs) || contains(errorValues, as)) {
            return "已查询" + house + "阀门：反馈异常，已按计划创建报修单。任务完成。";
        }
        return "已查询" + house + "阀门：当前为「开启」、无故障，无需开阀或报修，供热设备状态正常。任务完成。";
    }

    private static boolean contains(List<String> list, String val) {
        return val != null && list != null && list.contains(val);
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }
}
