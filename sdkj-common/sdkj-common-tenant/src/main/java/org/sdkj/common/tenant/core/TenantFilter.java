package org.sdkj.common.tenant.core;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.model.LoginUser;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.satoken.utils.LoginHelper;
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
        // 在 SaToken 过滤器之后执行，确保 SaToken 上下文已初始化
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        registration.setName("tenantFilter");
        return registration;
    }

    static class TenantFilterInner implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            String path = normalizePath(req);
            boolean pushed = false;

            try {
                if (isTenantRoute(path)) {
                    String tenantCode = req.getHeader(TenantDataSourceHelper.TENANT_HEADER);
                    if (StringUtils.isBlank(tenantCode) && !isPublicPath(path)) {
                        tenantCode = resolveTenantCode(req);
                    }
                    pushed = TenantDataSourceHelper.pushTenant(tenantCode);
                }
                chain.doFilter(request, response);
            } finally {
                TenantDataSourceHelper.clearTenant(pushed);
            }
        }

        private String resolveTenantCode(HttpServletRequest req) {
            String tokenValue = StpUtil.getTokenValue();
            if (StringUtils.isBlank(tokenValue)) {
                return null;
            }
            try {
                SaSession tokenSession = StpUtil.getTokenSessionByToken(tokenValue);
                Object tenantCode = tokenSession.get("tenantCode");
                if (tenantCode == null) {
                    Object loginUser = tokenSession.get(LoginHelper.LOGIN_USER_KEY);
                    if (loginUser instanceof LoginUser user) {
                        tenantCode = user.getTenantId();
                    }
                }
                return tenantCode == null ? null : tenantCode.toString();
            } catch (Exception e) {
                log.debug("无法从 token 解析租户上下文: {}", e.getMessage());
                return null;
            }
        }

        private String normalizePath(HttpServletRequest req) {
            String path = req.getRequestURI();
            String contextPath = req.getContextPath();
            if (StringUtils.isNotBlank(contextPath) && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }
            return StringUtils.isBlank(path) ? "/" : path;
        }

        private boolean isTenantRoute(String path) {
            return path.startsWith("/thermal/")
                || path.startsWith("/dashboard/")
                || path.startsWith("/api/iot/")
                || path.startsWith("/api/returnControl/")
                || path.startsWith("/ai/");
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
                || path.startsWith("/api/iot/")
                || path.startsWith("/api/returnControl/")
                || path.startsWith("/thermal/iot/")
                || path.equals("/thermal/wechat/pay/notify")
                || path.equals("/thermal/wechat/pay/refundNotify");
        }
    }
}
