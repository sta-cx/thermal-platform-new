package org.sdkj.ai.context;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("dev")
class BranchEvaluatorTest {

    private final BranchEvaluator evaluator = new BranchEvaluator();
    private final List<String> closed = List.of("2", "CLOSE", "CLOSED");
    private final List<String> error = List.of("3", "ERROR");

    private EntityFact valveFact(String valveStatus, Object actualStatus) {
        Map<String, Object> keys = new HashMap<>();
        keys.put("valveStatus", valveStatus);
        if (actualStatus != null) keys.put("actualStatus", actualStatus);
        return new EntityFact("f", "getByHouseId", keys, Instant.now(), Instant.now().plusSeconds(60));
    }

    private TaskStep stepWith(List<Branch> branches) {
        return new TaskStep("s1", "getByHouseId", "查阀门", Map.of(), branches, "WAITING");
    }

    @Test
    void valveClosedBranchWins() {
        var step = stepWith(List.of(
            new Branch("valveClosed", "s2"),
            new Branch("valveError", "s3"),
            new Branch("always", "END")));
        var facts = List.of(valveFact("2", null));
        assertEquals("s2", evaluator.next(step, facts, closed, error));
    }

    @Test
    void valveErrorBranchByActualStatus() {
        var step = stepWith(List.of(
            new Branch("valveClosed", "s2"),
            new Branch("valveError", "s3"),
            new Branch("always", "END")));
        var facts = List.of(valveFact("1", 3));      // 开但 actualStatus=3 故障
        assertEquals("s3", evaluator.next(step, facts, closed, error));
    }

    @Test
    void fallsThroughToAlways() {
        var step = stepWith(List.of(
            new Branch("valveClosed", "s2"),
            new Branch("always", "END")));
        var facts = List.of(valveFact("1", null));   // 开，非关非故障
        assertEquals("END", evaluator.next(step, facts, closed, error));
    }

    @Test
    void noBranchMatchesReturnsEnd() {
        var step = stepWith(List.of(new Branch("valveClosed", "s2")));
        var facts = List.of(valveFact("1", null));
        assertEquals("END", evaluator.next(step, facts, closed, error));
    }

    @Test
    void branchOrderRespected() {
        var step = stepWith(List.of(
            new Branch("always", "FIRST"),
            new Branch("valveClosed", "s2")));
        assertEquals("FIRST", evaluator.next(step, List.of(valveFact("2", null)), closed, error));
    }
}
