package org.sdkj.ai.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.ai.tools.dispatcher.ToolCallResult.ToolExecResult;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class FactExtractorTest {

    private final FactExtractor extractor = new FactExtractor(new ObjectMapper());

    private ToolExecResult res(String tool, String json) {
        return ToolExecResult.builder().toolName(tool).resultJson(json).build();
    }

    @Test
    void extractsScalarFieldsFromSingleObject() {
        var facts = extractor.extract(List.of(
            res("getByHouseId", "{\"valveId\":456,\"houseId\":123,\"valveStatus\":\"2\",\"roomTemp\":16.5}")
        ), Duration.ofMinutes(30));
        assertEquals(1, facts.size());
        var keys = facts.get(0).keys();
        assertEquals(123, ((Number) keys.get("houseId")).intValue());
        assertEquals(456, ((Number) keys.get("valveId")).intValue());
        assertEquals("2", keys.get("valveStatus"));
        assertEquals("getByHouseId", facts.get(0).sourceTool());
    }

    @Test
    void extractsOneFactPerArrayElement() {
        var facts = extractor.extract(List.of(
            res("queryUnpaid", "[{\"expenseId\":1,\"houseId\":7},{\"expenseId\":2,\"houseId\":7}]")
        ), Duration.ofMinutes(30));
        assertEquals(2, facts.size());
        assertEquals(1, ((Number) facts.get(0).keys().get("expenseId")).intValue());
        assertEquals(2, ((Number) facts.get(1).keys().get("expenseId")).intValue());
    }

    @Test
    void toleratesNullAndNonObjectJson() {
        var facts = extractor.extract(List.of(
            res("getByHouseId", "null"),
            res("x", "{\"error\":\"boom\"}")
        ), Duration.ofMinutes(30));
        assertEquals(1, facts.size(), "null 跳过；error 对象仍抽为一条 fact（含 error 字段）");
    }

    @Test
    void deriveFocusPicksByPriority() {
        var facts = extractor.extract(List.of(
            res("getById", "{\"stationId\":9,\"name\":\"X站\"}"),
            res("getByHouseId", "{\"valveId\":456,\"houseId\":123}")
        ), Duration.ofMinutes(30));
        FocusEntity focus = extractor.deriveFocus(facts,
            List.of("houseId", "valveId", "stationId"),
            List.of("house", "station", "valve"));
        assertNotNull(focus);
        assertEquals("house", focus.entityType(), "house 优先级最高");
        assertEquals(123L, focus.entityId());
        assertTrue(focus.attrs().containsKey("valveId"), "focus.attrs 带该实体的衍生字段");
    }

    @Test
    void deriveFocusReturnsNullWhenNoPriorityKey() {
        var facts = extractor.extract(List.of(
            res("foo", "{\"meterNum\":\"M-1\"}")
        ), Duration.ofMinutes(30));
        assertNull(extractor.deriveFocus(facts,
            List.of("houseId", "valveId", "stationId"),
            List.of("house", "station", "valve")));
    }
}
