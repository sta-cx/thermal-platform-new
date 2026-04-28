package org.sdkj.common.tenant.core;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步线程池租户上下文传递配置
 */
@Slf4j
@Configuration
public class TenantAsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("async-tenant-");
        executor.setTaskDecorator(new TenantTaskDecorator());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    static class TenantTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            String tenantCode = TenantContextHolder.getTenantCode();
            String dsKey = DynamicDataSourceContextHolder.peek();
            return () -> {
                boolean pushed = false;
                try {
                    if (tenantCode != null) {
                        TenantContextHolder.setTenantCode(tenantCode);
                    }
                    if (dsKey != null) {
                        DynamicDataSourceContextHolder.push(dsKey);
                        pushed = true;
                    }
                    runnable.run();
                } finally {
                    if (pushed) {
                        DynamicDataSourceContextHolder.poll();
                    }
                    TenantContextHolder.clear();
                }
            };
        }
    }
}
