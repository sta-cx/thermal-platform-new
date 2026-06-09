package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.registry.ToolMetadata;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class PlanServiceTest {

    private final PlanService svc = new PlanService(null, null, new AiProperties(),
        new io.micrometer.core.instrument.simple.SimpleMeterRegistry(), null);

    private ToolMetadata tool(String method, RiskLevel risk) {
        return new ToolMetadata("bean", method, "bean." + method, "desc", risk, false, "", null, null);
    }

    private Function<String, ToolMetadata> registryOf(Map<String, ToolMetadata> map) {
        return map::get;
    }

    // ===== 高频模板触发 =====

    @Test
    void recognizesHeatingComplaintIntent() {
        assertTrue(svc.isHeatingComplaint("帮我处理一下 3-201 的供热问题"));
        assertTrue(svc.isHeatingComplaint("这户暖气不热，处理下"));
    }

    @Test
    void rejectsNonComplaint() {
        assertFalse(svc.isHeatingComplaint("查一下今天的换热站运行情况"));
        assertFalse(svc.isHeatingComplaint("二网热力平衡是什么"));
    }

    @Test
    void buildsHeatingComplaintTemplate() {
        TaskState ts = svc.buildHeatingComplaintTask(123L);
        assertEquals("handle_heating_complaint", ts.getTaskType());
        assertEquals(3, ts.getSteps().size());
        assertEquals("s1", ts.getCurrentStepId());
        assertEquals("getByHouseId", ts.getSteps().get(0).getToolName());
        assertEquals(123L, ((Number) ts.getSteps().get(0).getPresetArgs().get("houseId")).longValue());
        assertEquals("AWAITING_APPROVAL", ts.getStatus());
    }

    // ===== 通用多步意图粗筛 =====

    @Test
    void detectsMultiStepByConnector() {
        assertTrue(svc.looksLikeMultiStep("先查房屋5的欠费然后标记已缴"));
        assertTrue(svc.looksLikeMultiStep("查一下阀门状态，再帮我开阀"));
    }

    @Test
    void detectsMultiStepByActionPlusNoun() {
        assertTrue(svc.looksLikeMultiStep("处理这户的欠费"));
        assertTrue(svc.looksLikeMultiStep("解决房屋3的报修"));
    }

    @Test
    void rejectsNonMultiStep() {
        assertFalse(svc.looksLikeMultiStep("二网热力平衡是什么"));
        assertFalse(svc.looksLikeMultiStep("你好"));
        assertFalse(svc.looksLikeMultiStep("处理一下")); // 有动词无名词
    }

    // ===== 线性计划装配（校验）=====

    @Test
    void assemblesLinearPlanFromValidDraft() {
        Map<String, ToolMetadata> reg = Map.of(
            "queryArrears", tool("queryArrears", RiskLevel.LOW),
            "markPaid", tool("markPaid", RiskLevel.HIGH));
        var steps = List.of(
            new PlanService.DraftStep("queryArrears", "查询欠费", List.of(new PlanService.ArgKV("houseId", "5"))),
            new PlanService.DraftStep("markPaid", "标记已缴", List.of()));
        TaskState ts = PlanService.assembleLinearPlan("处理欠费", steps, registryOf(reg), 5);
        assertNotNull(ts);
        assertEquals("llm_dynamic", ts.getTaskType());
        assertEquals(2, ts.getSteps().size());
        assertEquals("s1", ts.getCurrentStepId());
        assertEquals("END", ts.getSteps().get(1).getBranches().get(0).gotoStepId());
        assertEquals("s2", ts.getSteps().get(0).getBranches().get(0).gotoStepId());
        assertEquals(5L, ts.getSteps().get(0).getPresetArgs().get("houseId"));
        assertEquals("AWAITING_APPROVAL", ts.getStatus());
    }

    @Test
    void rejectsPlanWithCriticalTool() {
        Map<String, ToolMetadata> reg = Map.of(
            "queryArrears", tool("queryArrears", RiskLevel.LOW),
            "batchDispatch", tool("batchDispatch", RiskLevel.CRITICAL));
        var steps = List.of(
            new PlanService.DraftStep("queryArrears", "查询", List.of()),
            new PlanService.DraftStep("batchDispatch", "批量下发", List.of()));
        assertNull(PlanService.assembleLinearPlan("批量", steps, registryOf(reg), 5));
    }

    @Test
    void dropsUnknownToolAndRejectsIfTooFew() {
        Map<String, ToolMetadata> reg = Map.of("queryArrears", tool("queryArrears", RiskLevel.LOW));
        var steps = List.of(
            new PlanService.DraftStep("queryArrears", "查询", List.of()),
            new PlanService.DraftStep("noSuchTool", "幻觉工具", List.of()));
        // 只剩 1 个有效步 → 不足两步 → null
        assertNull(PlanService.assembleLinearPlan("x", steps, registryOf(reg), 5));
    }

    @Test
    void c4_coerceValTypes() {
        assertEquals(123L, PlanService.coerceVal("123"));
        assertEquals(1.5d, PlanService.coerceVal("1.5"));
        assertEquals(Boolean.TRUE, PlanService.coerceVal("true"));
        assertEquals("OPEN", PlanService.coerceVal("OPEN"));
        assertNull(PlanService.coerceVal(null));
    }
}
