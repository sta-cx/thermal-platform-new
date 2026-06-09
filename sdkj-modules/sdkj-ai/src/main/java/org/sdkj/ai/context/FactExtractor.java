package org.sdkj.ai.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.tools.dispatcher.ToolCallResult.ToolExecResult;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * 通用字段扫描：把 Tool 结果 JSON 的标量字段整体抽成 EntityFact，不按 tool 名硬编码。
 * 新增任意 Tool 无需改本类（满足"持续新增 Tool"约束）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FactExtractor {

    private final ObjectMapper objectMapper;

    /** 从 LOW 工具结果抽取 facts。每个结果对象 → 一条 fact；数组 → 每元素一条。 */
    public List<EntityFact> extract(List<ToolExecResult> results, Duration ttl) {
        List<EntityFact> out = new ArrayList<>();
        if (results == null) return out;
        Instant now = Instant.now();
        Instant expire = now.plus(ttl);
        for (ToolExecResult r : results) {
            String json = r.getResultJson();
            if (json == null || json.isBlank() || "null".equals(json.trim())) continue;
            try {
                JsonNode node = objectMapper.readTree(json);
                if (node.isArray()) {
                    for (JsonNode el : node) addFact(out, r.getToolName(), el, now, expire);
                } else {
                    addFact(out, r.getToolName(), node, now, expire);
                }
            } catch (Exception e) {
                log.debug("[FactExtractor] skip unparseable result of {}: {}", r.getToolName(), e.getMessage());
            }
        }
        return out;
    }

    private void addFact(List<EntityFact> out, String tool, JsonNode obj, Instant now, Instant expire) {
        if (obj == null || !obj.isObject()) return;
        Map<String, Object> keys = new LinkedHashMap<>();
        obj.fields().forEachRemaining(e -> {
            JsonNode v = e.getValue();
            if (v.isValueNode()) {                       // 仅标量字段
                if (v.isIntegralNumber()) keys.put(e.getKey(), v.longValue());
                else if (v.isFloatingPointNumber()) keys.put(e.getKey(), v.doubleValue());
                else if (v.isBoolean()) keys.put(e.getKey(), v.booleanValue());
                else if (!v.isNull()) keys.put(e.getKey(), v.asText());
            }
        });
        if (keys.isEmpty()) return;
        out.add(new EntityFact(UUID.randomUUID().toString(), tool, keys, now, expire));
    }

    /**
     * 按 focusPriority 选主体实体。priority 元素 type 对应主键 = type+"Id"。
     * 从最新 fact 往旧找首个含该主键的 fact。
     */
    public FocusEntity deriveFocus(List<EntityFact> facts, List<String> entityKeys, List<String> focusPriority) {
        if (facts == null || facts.isEmpty()) return null;
        for (String type : focusPriority) {
            String key = type + "Id";
            if (!entityKeys.contains(key)) continue;
            for (int i = facts.size() - 1; i >= 0; i--) {
                EntityFact f = facts.get(i);
                Object val = f.keys().get(key);
                if (val instanceof Number n) {
                    return new FocusEntity(type, n.longValue(), type + "#" + n.longValue(),
                        f.keys(), Instant.now());
                }
            }
        }
        return null;
    }
}
