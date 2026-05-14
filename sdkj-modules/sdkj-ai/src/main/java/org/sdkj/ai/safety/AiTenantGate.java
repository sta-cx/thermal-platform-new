package org.sdkj.ai.safety;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.exception.AiDisabledException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 租户级 AI 总闸校验。
 * <p>
 * sys_tenant 在 master 库。使用 @DS("master") 方法注解 + JdbcTemplate 直接查询，
 * 避免 sdkj-ai → sdkj-system 循环依赖（sdkj-system 已依赖 sdkj-ai）。
 */
@Component
@RequiredArgsConstructor
public class AiTenantGate {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 校验当前租户的 AI 总闸是否开启。关闭则抛 AiDisabledException(由 Controller
     * ExceptionHandler 映射到 503)。
     * <p>
     * 注意：tenantId 在 sys_tenant 表中是 VARCHAR(20) 的 tenant_id 字段，
     * 但主键是 BIGINT id。这里用 tenant_id 列查询。
     */
    @DS("master")
    public void requireEnabled(String tenantId) {
        if (tenantId == null) {
            return;
        }
        Boolean enabled = jdbcTemplate.query(
            "SELECT ai_enabled FROM sys_tenant WHERE tenant_id = ? AND del_flag = '0'",
            rs -> {
                if (rs.next()) {
                    return rs.getBoolean("ai_enabled");
                }
                return null;
            },
            tenantId
        );
        if (enabled == null || !enabled) {
            throw new AiDisabledException();
        }
    }
}
