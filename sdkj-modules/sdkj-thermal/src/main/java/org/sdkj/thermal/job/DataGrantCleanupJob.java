package org.sdkj.thermal.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.SpringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * pr_data_grant 墓碑清理 Spring @Scheduled 入口。
 *
 * 流程:
 *   Phase 1 全租户预扫合计待删行数
 *   Phase 2 上限检查(超过 max-delete-per-run 则跳过整个 Job + WARN)
 *   Phase 3 逐租户分批物理删(单租户 try-catch 隔离)
 *
 * 不引入 Micrometer,只走 SLF4J log。对齐 AiLogCleanupJob 的极简模式。
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(DataGrantCleanupProperties.class)
public class DataGrantCleanupJob {

    private final DataGrantCleanupProperties props;
    private final DataGrantCleanupRunner runner;

    @Scheduled(cron = "${sdkj.data-grant-cleanup.cron:0 30 3 * * ?}")
    public void run() {
        if (!props.isEnabled()) {
            log.debug("DataGrantCleanupJob disabled, skip");
            return;
        }
        long start = System.currentTimeMillis();
        log.info("DataGrantCleanupJob start: enabled=true, retentionDays={}, batchSize={}, maxDeletePerRun={}",
            props.getRetentionDays(), props.getBatchSize(), props.getMaxDeletePerRun());

        List<String> tenantIds = queryActiveTenants();

        // Phase 1: 全租户预扫
        Map<String, Long> pending = new LinkedHashMap<>();
        long totalPending = 0;
        for (String tid : tenantIds) {
            try {
                long count = runner.countPending(tid, props.getRetentionDays());
                pending.put(tid, count);
                totalPending += count;
            } catch (Exception ex) {
                log.error("DataGrantCleanupJob: tenant {} countPending failed", tid, ex);
                pending.put(tid, 0L);
            }
        }

        // Phase 2: 上限检查
        if (totalPending > props.getMaxDeletePerRun()) {
            log.warn("DataGrantCleanupJob skipped: totalPending={} exceeds limit {}; per-tenant={}",
                totalPending, props.getMaxDeletePerRun(), pending);
            return;
        }
        if (totalPending == 0) {
            log.info("DataGrantCleanupJob: no tombstones to clean");
            return;
        }

        // Phase 3: 逐租户分批删
        long totalDeleted = 0;
        for (Map.Entry<String, Long> e : pending.entrySet()) {
            if (e.getValue() == 0) {
                continue;
            }
            try {
                long deleted = runner.deleteBatched(
                    e.getKey(), props.getRetentionDays(), props.getBatchSize());
                totalDeleted += deleted;
            } catch (Exception ex) {
                log.error("DataGrantCleanupJob: tenant {} deleteBatched failed", e.getKey(), ex);
            }
        }

        log.info("DataGrantCleanupJob done: tenants={}, totalPending={}, totalDeleted={}, costMs={}",
            tenantIds.size(), totalPending, totalDeleted, System.currentTimeMillis() - start);
    }

    /**
     * 显式走 master 库:SpringUtils.getBean(DataSource.class) 返回 DynamicRoutingDataSource,
     * JdbcTemplate 通过 ThreadLocal 路由,Job 启动初期 ThreadLocal 为空 → 落到
     * spring.datasource.dynamic.primary: master 配置的主库。
     * 对齐 QuartzJobInitRunner 第 46 行的现成模式。
     */
    private List<String> queryActiveTenants() {
        DataSource masterDs = SpringUtils.getBean(DataSource.class);
        return new JdbcTemplate(masterDs).queryForList(
            "SELECT tenant_id FROM sys_tenant WHERE status='0' AND del_flag='0' AND db_url IS NOT NULL",
            String.class);
    }
}
