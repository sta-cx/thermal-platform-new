package org.sdkj.common.mybatis.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.common.mybatis.helper.OrgPermissionHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class OrgPermissionAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        OrgPermissionHelper.setPermission(getOrgPermissionAnnotation(target, method));
        try {
            return invocation.proceed();
        } finally {
            OrgPermissionHelper.removePermission();
        }
    }

    private OrgPermission getOrgPermissionAnnotation(Object target, Method method) {
        OrgPermission permission = method.getAnnotation(OrgPermission.class);
        if (permission != null) {
            return permission;
        }
        Class<?> targetClass = target.getClass();
        if (Proxy.isProxyClass(targetClass)) {
            targetClass = targetClass.getInterfaces()[0];
        }
        return targetClass.getAnnotation(OrgPermission.class);
    }
}
