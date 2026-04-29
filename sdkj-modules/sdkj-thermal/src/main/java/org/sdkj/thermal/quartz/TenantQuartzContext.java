package org.sdkj.thermal.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.tenant.core.TenantDataSourceHelper;

/**
 * Restores tenant datasource context for Quartz jobs.
 */
final class TenantQuartzContext {

    private TenantQuartzContext() {
    }

    static boolean push(JobExecutionContext context) {
        JobDataMap data = context.getMergedJobDataMap();
        String tenantCode = data.getString("tenantCode");
        if (StringUtils.isBlank(tenantCode)) {
            throw new IllegalStateException("Quartz job missing tenantCode in JobDataMap");
        }
        return TenantDataSourceHelper.pushTenant(tenantCode);
    }

    static void clear(boolean pushed) {
        TenantDataSourceHelper.clearTenant(pushed);
    }
}
