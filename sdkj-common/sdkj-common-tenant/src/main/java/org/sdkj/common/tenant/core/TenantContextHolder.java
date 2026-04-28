package org.sdkj.common.tenant.core;

/**
 * 租户上下文持有者，基于 ThreadLocal 保存当前请求的租户编码
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();

    public static void setTenantCode(String tenantCode) {
        TENANT_CODE.set(tenantCode);
    }

    public static String getTenantCode() {
        return TENANT_CODE.get();
    }

    public static void clear() {
        TENANT_CODE.remove();
    }

    /**
     * 包装 Runnable，传递租户上下文到异步线程
     */
    public static Runnable wrap(Runnable runnable) {
        String tenantCode = TENANT_CODE.get();
        return () -> {
            String previous = TENANT_CODE.get();
            try {
                if (tenantCode != null) {
                    TENANT_CODE.set(tenantCode);
                }
                runnable.run();
            } finally {
                if (previous != null) {
                    TENANT_CODE.set(previous);
                } else {
                    TENANT_CODE.remove();
                }
            }
        };
    }
}
