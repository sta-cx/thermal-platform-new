package org.sdkj.common.mybatis.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrgPermission {
    String value() default "org_id";
}
