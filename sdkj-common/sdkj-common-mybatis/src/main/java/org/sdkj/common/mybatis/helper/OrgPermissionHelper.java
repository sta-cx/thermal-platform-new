package org.sdkj.common.mybatis.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sdkj.common.mybatis.annotation.OrgPermission;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrgPermissionHelper {

    private static final ThreadLocal<OrgPermission> PERMISSION_CACHE = new ThreadLocal<>();

    public static OrgPermission getPermission() {
        return PERMISSION_CACHE.get();
    }

    public static void setPermission(OrgPermission permission) {
        PERMISSION_CACHE.set(permission);
    }

    public static void removePermission() {
        PERMISSION_CACHE.remove();
    }
}
