package org.sdkj.common.tenant.core;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;

/**
 * Tenant datasource switch helpers for database-per-tenant mode.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantDataSourceHelper {

    public static final String TENANT_HEADER = "X-Tenant-Code";

    public static String dataSourceName(String tenantCode) {
        return "tenant_" + tenantCode;
    }

    public static boolean pushTenant(String tenantCode) {
        if (StringUtils.isBlank(tenantCode)) {
            return false;
        }
        TenantContextHolder.setTenantCode(tenantCode);
        DynamicDataSourceContextHolder.push(dataSourceName(tenantCode));
        return true;
    }

    public static void clearTenant(boolean pushed) {
        if (pushed) {
            DynamicDataSourceContextHolder.poll();
        }
        TenantContextHolder.clear();
    }
}
