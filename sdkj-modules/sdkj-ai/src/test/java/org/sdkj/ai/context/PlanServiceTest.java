package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class PlanServiceTest {

    private final PlanService svc = new PlanService(null, null, null,
        new io.micrometer.core.instrument.simple.SimpleMeterRegistry());

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
}
