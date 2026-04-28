package org.sdkj.common.tenant.core;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.utils.SpringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 启动时从 master 库读取租户列表，为每个租户注册动态数据源 tenant_{tenantId}
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class TenantDataSourceInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        DynamicRoutingDataSource dynamicDs = (DynamicRoutingDataSource) SpringUtils.getBean(DataSource.class);

        JdbcTemplate jdbc = new JdbcTemplate(dynamicDs.getDataSource("master"));
        List<Map<String, Object>> tenants = jdbc.queryForList(
            "SELECT tenant_id, db_url, db_username, db_password, db_driver FROM sys_tenant WHERE status = '0' AND del_flag = '0'"
        );

        for (Map<String, Object> tenant : tenants) {
            String tenantId = tenant.get("tenant_id").toString();
            String dbUrl = tenant.get("db_url") != null ? tenant.get("db_url").toString() : null;
            if (dbUrl == null || dbUrl.isEmpty()) {
                log.warn("租户 {} 未配置数据库连接，跳过", tenantId);
                continue;
            }

            String dsName = "tenant_" + tenantId;
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(dbUrl);
            ds.setUsername(tenant.get("db_username").toString());
            ds.setPassword(tenant.get("db_password").toString());
            ds.setDriverClassName(tenant.get("db_driver") != null ? tenant.get("db_driver").toString() : "com.mysql.cj.jdbc.Driver");
            ds.setMaximumPoolSize(20);
            ds.setMinimumIdle(5);
            ds.setConnectionTimeout(30000);
            ds.setIdleTimeout(300000);
            ds.setMaxLifetime(1800000);
            ds.setConnectionTestQuery("SELECT 1");

            dynamicDs.addDataSource(dsName, ds);
            log.info("注册租户数据源: {} -> {}", dsName, dbUrl);
        }

        log.info("租户数据源初始化完成, 共加载 {} 个租户", tenants.size());
    }
}
