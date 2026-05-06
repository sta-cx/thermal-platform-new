package org.sdkj.common.mybatis.aspect;

import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SuppressWarnings("all")
public class OrgPermissionPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (method.isAnnotationPresent(OrgPermission.class)) {
            return true;
        }
        Class<?> targetClassRef = targetClass;
        if (Proxy.isProxyClass(targetClassRef)) {
            targetClassRef = targetClass.getInterfaces()[0];
        }
        return targetClassRef.isAnnotationPresent(OrgPermission.class);
    }
}
