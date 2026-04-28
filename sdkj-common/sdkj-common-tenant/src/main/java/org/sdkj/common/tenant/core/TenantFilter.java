package org.sdkj.common.tenant.core;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * 租户过滤器：每次请求自动切换到对应用户的租户数据源
 */
@Slf4j
@Configuration
public class TenantFilter {

    @Bean
    public FilterRegistrationBean<TenantFilterInner> tenantFilterRegistration() {
        FilterRegistrationBean<TenantFilterInner> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantFilterInner());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        registration.setName("tenantFilter");
        return registration;
    }

    static class TenantFilterInner implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            String path = req.getRequestURI();
            boolean pushed = false;

            try {
                if (isPublicPath(path)) {
                    chain.doFilter(request, response);
                    return;
                }

                if (StpUtil.isLogin()) {
                    Object tenantCode = StpUtil.getSession().get("tenantCode");
                    if (tenantCode != null) {
                        String code = tenantCode.toString();
                        TenantContextHolder.setTenantCode(code);
                        String dsName = "tenant_" + code;
                        DynamicDataSourceContextHolder.push(dsName);
                        pushed = true;
                    }
                }
                chain.doFilter(request, response);
            } finally {
                if (pushed) {
                    DynamicDataSourceContextHolder.poll();
                }
                TenantContextHolder.clear();
            }
        }

        private boolean isPublicPath(String path) {
            return path.startsWith("/auth/")
                || path.startsWith("/code")
                || path.startsWith("/actuator/")
                || path.startsWith("/doc.html")
                || path.startsWith("/webjars/")
                || path.startsWith("/swagger-")
                || path.startsWith("/v3/")
                || path.startsWith("/favicon.ico")
                || path.startsWith("/thermal/iot/")
                || path.startsWith("/thermal/wechat/")
                || path.startsWith("/thermal/wxma/");
        }
    }
}
