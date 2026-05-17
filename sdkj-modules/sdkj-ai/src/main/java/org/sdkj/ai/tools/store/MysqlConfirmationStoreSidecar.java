package org.sdkj.ai.tools.store;

import org.sdkj.ai.AiConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.ai.domain.AiPendingToolCall;
import org.sdkj.ai.mapper.AiPendingToolCallMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MySQL 审计 Sidecar — 异步,与 Redis 主存储完全解耦。
 *
 * Redis 不可用 / Key 过期时,可从 MySQL 表回查 Tool Call 的"最终状态",但不再支持 transition。
 * 设计上保持简单:每次 save / transition 后,Sidecar 完整 upsert 一次表行;不做增量更新。
 *
 * 注意:MySQL 表存的是历史完整流水,审计回放、合规出报必读这里(Redis 是 TTL 缓存)。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MysqlConfirmationStoreSidecar {

    private final AiPendingToolCallMapper mapper;
    private final ObjectMapper objectMapper;

    @Async
    public void upsert(PendingToolCall call) {
        DynamicDataSourceContextHolder.push(AiConstants.DS_MASTER);
        try {
            AiPendingToolCall row = toRow(call);
            AiPendingToolCall existing = mapper.selectOne(
                com.baomidou.mybatisplus.core.toolkit.Wrappers
                    .<AiPendingToolCall>lambdaQuery()
                    .eq(AiPendingToolCall::getCallId, call.getCallId())
            );
            if (existing == null) {
                mapper.insert(row);
            } else {
                row.setId(existing.getId());
                mapper.updateById(row);
            }
        } catch (Exception e) {
            // 审计写失败不能影响主流程,只记日志
            log.error("[MysqlConfirmationStoreSidecar] upsert failed for {}: {}",
                call.getCallId(), e.getMessage(), e);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    private AiPendingToolCall toRow(PendingToolCall call) {
        try {
            AiPendingToolCall row = new AiPendingToolCall();
            row.setCallId(call.getCallId());
            row.setTenantId(call.getTenantId());
            row.setUserId(call.getUserId());
            row.setSessionId(call.getSessionId());
            row.setMessageId(call.getMessageId());
            row.setToolName(call.getToolName());
            row.setRiskLevel(call.getRisk().name());
            row.setArguments(objectMapper.writeValueAsString(call.getArguments()));
            row.setEffectiveArgs(call.getEffectiveArgs() == null
                ? null : objectMapper.writeValueAsString(call.getEffectiveArgs()));
            row.setStatus(call.getStatus().name());
            row.setResult(call.getResult());
            row.setCreatedTime(toDate(call.getCreatedAt()));
            row.setDecidedTime(toDate(call.getDecidedAt()));
            row.setExecutedTime(toDate(call.getExecutedAt()));
            row.setExpireTime(toDate(call.getExpireAt()));
            return row;
        } catch (Exception e) {
            throw new IllegalStateException("serialize pending call for db failed", e);
        }
    }

    private Date toDate(java.time.Instant i) {
        return i == null ? null : Date.from(i);
    }
}
