package org.sdkj.ai.controller;

import io.qdrant.client.QdrantClient;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI 模块健康检查端点。
 * <p>
 * 检查 Qdrant 连通性（可选 — 未配置时标记为 DEGRADED 而非失败）。
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiHealthController {

    @Autowired(required = false)
    private QdrantClient qdrantClient;

    @GetMapping("/health")
    public R<Map<String, Object>> health() {
        boolean qdrantOk = false;
        if (qdrantClient != null) {
            try {
                qdrantClient.listCollectionsAsync().get(2, TimeUnit.SECONDS);
                qdrantOk = true;
            } catch (Exception e) {
                qdrantOk = false;
                log.warn("Qdrant health check failed", e);
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", qdrantOk ? "UP" : "DEGRADED");
        data.put("module", "sdkj-ai");
        data.put("phase", "2A");
        data.put("qdrant", qdrantOk);
        return R.ok(data);
    }
}
