package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class OrchestrationSummaryTest {

    private static final List<String> CLOSED = List.of("2", "CLOSE", "CLOSED");
    private static final List<String> ERROR = List.of("3", "ERROR");

    private TaskState heatingTask() {
        TaskState ts = new TaskState();
        ts.setTaskType("handle_heating_complaint");
        ts.setTitle("处理供热问题（房屋 1）");
        return ts;
    }

    private EntityFact valveFact(Map<String, Object> keys) {
        return new EntityFact("f1", "getByHouseId", keys, Instant.now(), Instant.now());
    }

    @Test
    void heatingHealthyValveSaysNoActionNeeded() {
        EntityFact vf = valveFact(Map.of("houseId", 1L, "valveStatus", "1"));
        String s = OrchestrationSummary.build(heatingTask(), vf, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("开启"));
        assertTrue(s.contains("无需开阀或报修"));
        assertTrue(s.contains("房屋1"));
    }

    @Test
    void heatingClosedValveSaysOpened() {
        EntityFact vf = valveFact(Map.of("houseId", 1L, "valveStatus", "2"));
        String s = OrchestrationSummary.build(heatingTask(), vf, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("关闭"));
        assertTrue(s.contains("开阀"));
    }

    @Test
    void heatingErrorValveSaysRepair() {
        EntityFact vf = valveFact(Map.of("houseId", 1L, "valveStatus", "3"));
        String s = OrchestrationSummary.build(heatingTask(), vf, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("报修"));
    }

    @Test
    void heatingNoValveArchive() {
        String s = OrchestrationSummary.build(heatingTask(), null, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("未查询到"));
        assertTrue(s.contains("阀门档案"));
    }

    @Test
    void abortedWithoutReason() {
        String s = OrchestrationSummary.build(heatingTask(), null, "ABORTED", null, CLOSED, ERROR);
        assertTrue(s.contains("已取消"));
    }

    @Test
    void abortedWithReason() {
        String s = OrchestrationSummary.build(heatingTask(), null, "ABORTED", "包含禁止操作，已中止", CLOSED, ERROR);
        assertTrue(s.contains("已中止"));
        assertTrue(s.contains("禁止操作"));
    }

    @Test
    void genericDonePlanRecapsCompletedSteps() {
        TaskState ts = new TaskState();
        ts.setTaskType("llm_dynamic");
        ts.setTitle("查欠费并标记已缴");
        TaskStep s1 = new TaskStep("s1", "queryArrears", "查询该户欠费", Map.of(), List.of(), "COMPLETED");
        TaskStep s2 = new TaskStep("s2", "markPaid", "标记费用已缴", Map.of(), List.of(), "COMPLETED");
        ts.setSteps(List.of(s1, s2));
        String s = OrchestrationSummary.build(ts, null, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("查询该户欠费"));
        assertTrue(s.contains("标记费用已缴"));
        assertTrue(s.contains("执行完成"));
    }

    @Test
    void genericDoneSkipsNonCompletedSteps() {
        TaskState ts = new TaskState();
        ts.setTaskType("llm_dynamic");
        ts.setTitle("两步任务");
        TaskStep s1 = new TaskStep("s1", "queryArrears", "已完成步骤", Map.of(), List.of(), "COMPLETED");
        TaskStep s2 = new TaskStep("s2", "markPaid", "未执行步骤", Map.of(), List.of(), "WAITING");
        ts.setSteps(List.of(s1, s2));
        String s = OrchestrationSummary.build(ts, null, "DONE", null, CLOSED, ERROR);
        assertTrue(s.contains("已完成步骤"));
        assertFalse(s.contains("未执行步骤"));
    }
}
