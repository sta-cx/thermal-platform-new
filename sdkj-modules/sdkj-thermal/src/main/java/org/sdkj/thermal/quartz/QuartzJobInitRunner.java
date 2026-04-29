package org.sdkj.thermal.quartz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.SpringUtils;
import org.sdkj.common.tenant.core.TenantDataSourceHelper;
import org.sdkj.thermal.domain.HtTasks;
import org.sdkj.thermal.service.IHtTasksService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class QuartzJobInitRunner implements CommandLineRunner {

    private final IHtTasksService tasksService;
    private final ThermalJobManager thermalJobManager;

    @Override
    public void run(String... args) {
        log.info("Initializing Quartz jobs from database...");
        int totalScheduled = 0;

        // 从 master 库获取所有启用的租户
        List<Map<String, Object>> tenants = getAllTenants();

        for (Map<String, Object> tenant : tenants) {
            String tenantId = tenant.get("tenant_id").toString();
            int scheduled = initTenantJobs(tenantId);
            totalScheduled += scheduled;
        }

        log.info("Quartz job initialization complete. {} tasks scheduled across {} tenants.", totalScheduled, tenants.size());
    }

    private List<Map<String, Object>> getAllTenants() {
        try {
            DataSource masterDs = SpringUtils.getBean(DataSource.class);
            JdbcTemplate jdbc = new JdbcTemplate(masterDs);
            return jdbc.queryForList(
                "SELECT tenant_id FROM sys_tenant WHERE status = '0' AND del_flag = '0' AND db_url IS NOT NULL"
            );
        } catch (Exception e) {
            log.error("Failed to query tenant list", e);
            return List.of();
        }
    }

    private int initTenantJobs(String tenantId) {
        boolean tenantPushed = TenantDataSourceHelper.pushTenant(tenantId);
        try {
            List<HtTasks> activeTasks = tasksService.lambdaQuery()
                .eq(HtTasks::getStatus, 1)
                .isNotNull(HtTasks::getCronExpression)
                .list();

            int successCount = 0;
            for (HtTasks task : activeTasks) {
                try {
                    thermalJobManager.addJob(task.getId());
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to schedule task id={} for tenant {}: {}", task.getId(), tenantId, e.getMessage());
                }
            }
            log.info("Tenant {}: {}/{} tasks scheduled.", tenantId, successCount, activeTasks.size());
            return successCount;
        } catch (Exception e) {
            log.error("Failed to init jobs for tenant {}", tenantId, e);
            return 0;
        } finally {
            TenantDataSourceHelper.clearTenant(tenantPushed);
        }
    }
}
