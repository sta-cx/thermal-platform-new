package org.sdkj.ai.tools.store;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * 待确认 Tool Call 存储抽象。
 *
 * 设计:Redis 主(快路径,advisor / 前端轮询、TTL 30 min)+ MySQL 异步审计 Sidecar(完整状态机,90 天保留)。
 * Redis 失败 / Key 已 EXPIRED 时,Sidecar 提供回退查询(读 MySQL 表的最新状态)。
 */
public interface ConfirmationStore {

    void save(PendingToolCall call);

    Optional<PendingToolCall> findByCallId(String callId);

    /**
     * 原子状态迁移。返回更新后的 PendingToolCall(Redis Key 已 TTL 续期或删除)。
     * 若 callId 不存在 / 当前状态不允许该迁移,返回 Optional.empty。
     */
    Optional<PendingToolCall> transition(String callId,
                                          PendingToolCallStatus expected,
                                          PendingToolCallStatus to,
                                          Consumer<PendingToolCall> mutator);

    void delete(String callId);
}
