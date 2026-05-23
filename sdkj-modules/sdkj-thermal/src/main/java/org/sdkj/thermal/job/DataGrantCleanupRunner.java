package org.sdkj.thermal.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.tenant.core.TenantDataSourceHelper;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * pr_data_grant 墓碑清理单租户操作单元。
 *
 * 注意:本类直接用 PrDataGrantMapper 的原生 SQL 物理删墓碑,
 * 不要给 PrDataGrantMapper 加 @OrgPermission(它是权限源,不是被过滤的业务表)。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataGrantCleanupRunner {

    private final PrDataGrantMapper mapper;

    /**
     * 预扫:不动数据,仅统计当前租户库中早于阈值的墓碑数量。
     */
    public long countPending(String tenantId, int retentionDays) {
        boolean pushed = TenantDataSourceHelper.pushTenant(tenantId);
        try {
            return mapper.countTombstones(thresholdOf(retentionDays));
        } finally {
            TenantDataSourceHelper.clearTenant(pushed);
        }
    }

    /**
     * 分批物理删除:每批最多 batchSize 行,直到一次返回 < batchSize 结束。
     * 每批独立 SQL 调用,事务粒度由 MyBatis 默认控制(每次 @Delete 一个事务)。
     */
    public long deleteBatched(String tenantId, int retentionDays, int batchSize) {
        boolean pushed = TenantDataSourceHelper.pushTenant(tenantId);
        long total = 0;
        int batches = 0;
        try {
            Date threshold = thresholdOf(retentionDays);
            while (true) {
                int deleted = mapper.deleteTombstoneBatch(threshold, batchSize);
                total += deleted;
                batches++;
                if (deleted < batchSize) {
                    break;
                }
            }
            log.info("DataGrantCleanupJob: tenant={} deleted={} in {} batches", tenantId, total, batches);
            return total;
        } finally {
            TenantDataSourceHelper.clearTenant(pushed);
        }
    }

    /** 阈值时间 = 今天零点 - retentionDays 天。Job 跨午夜也用同一次取值,所有 batch 共享。 */
    private Date thresholdOf(int retentionDays) {
        return Date.from(
            LocalDate.now().minusDays(retentionDays)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }
}
