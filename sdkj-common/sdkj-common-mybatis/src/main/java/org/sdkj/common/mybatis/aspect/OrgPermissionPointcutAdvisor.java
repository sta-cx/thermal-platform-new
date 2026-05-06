package org.sdkj.common.mybatis.aspect;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

@SuppressWarnings("all")
public class OrgPermissionPointcutAdvisor extends AbstractPointcutAdvisor {

    private final Advice advice;
    private final Pointcut pointcut;

    public OrgPermissionPointcutAdvisor() {
        this.advice = new OrgPermissionAdvice();
        this.pointcut = new OrgPermissionPointcut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
