package org.sdkj.ai.context;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.AiConstants;
import org.sdkj.ai.mapper.AiChatSessionMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextDbSidecar {

    private final AiChatSessionMapper sessionMapper;

    @Async
    public void persist(Long sessionId, String json) {
        DynamicDataSourceContextHolder.push(AiConstants.DS_MASTER);
        try {
            sessionMapper.updateContextData(sessionId, json);
        } catch (Exception e) {
            log.error("[ContextDbSidecar] persist failed for session {}: {}", sessionId, e.getMessage());
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}
