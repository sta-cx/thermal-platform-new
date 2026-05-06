package org.sdkj.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.constant.CacheNames;
import org.sdkj.common.core.constant.TenantConstants;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.core.utils.MapstructUtils;
import org.sdkj.common.core.utils.SpringUtils;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.common.tenant.helper.TenantHelper;
import org.sdkj.system.domain.SysTenant;
import org.sdkj.system.domain.SysTenantPackage;
import org.sdkj.system.domain.SysTenantUser;
import org.sdkj.system.domain.bo.CreateDatabaseBo;
import org.sdkj.system.domain.bo.DbConnectionBo;
import org.sdkj.system.domain.bo.SysTenantBo;
import org.sdkj.system.domain.vo.SysTenantVo;
import org.sdkj.system.domain.vo.SysUserVo;
import org.sdkj.system.mapper.SysTenantMapper;
import org.sdkj.system.mapper.SysTenantPackageMapper;
import org.sdkj.system.mapper.SysTenantUserMapper;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.system.service.ISysTenantService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl implements ISysTenantService {

    private final SysTenantMapper baseMapper;
    private final SysTenantPackageMapper tenantPackageMapper;
    private final SysTenantUserMapper tenantUserMapper;
    private final SysUserMapper userMapper;

    @Override
    public SysTenantVo queryById(Long id) {
        SysTenantVo vo = baseMapper.selectVoById(id);
        fillDbParts(vo);
        return vo;
    }

    @Cacheable(cacheNames = CacheNames.SYS_TENANT, key = "#tenantId")
    @Override
    public SysTenantVo queryByTenantId(String tenantId) {
        SysTenantVo vo = baseMapper.selectVoOne(new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getTenantId, tenantId));
        fillDbParts(vo);
        return vo;
    }

    @Override
    public TableDataInfo<SysTenantVo> queryPageList(SysTenantBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysTenant> lqw = buildQueryWrapper(bo);
        Page<SysTenantVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(this::fillDbParts);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysTenantVo> queryList(SysTenantBo bo) {
        LambdaQueryWrapper<SysTenant> lqw = buildQueryWrapper(bo);
        List<SysTenantVo> list = baseMapper.selectVoList(lqw);
        list.forEach(this::fillDbParts);
        return list;
    }

    private LambdaQueryWrapper<SysTenant> buildQueryWrapper(SysTenantBo bo) {
        LambdaQueryWrapper<SysTenant> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getTenantId()), SysTenant::getTenantId, bo.getTenantId());
        lqw.like(StringUtils.isNotBlank(bo.getContactUserName()), SysTenant::getContactUserName, bo.getContactUserName());
        lqw.eq(StringUtils.isNotBlank(bo.getContactPhone()), SysTenant::getContactPhone, bo.getContactPhone());
        lqw.like(StringUtils.isNotBlank(bo.getCompanyName()), SysTenant::getCompanyName, bo.getCompanyName());
        lqw.like(StringUtils.isNotBlank(bo.getDomain()), SysTenant::getDomain, bo.getDomain());
        lqw.eq(bo.getPackageId() != null, SysTenant::getPackageId, bo.getPackageId());
        lqw.eq(bo.getExpireTime() != null, SysTenant::getExpireTime, bo.getExpireTime());
        lqw.eq(bo.getAccountCount() != null, SysTenant::getAccountCount, bo.getAccountCount());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), SysTenant::getStatus, bo.getStatus());
        lqw.orderByAsc(SysTenant::getId);
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertByBo(SysTenantBo bo) {
        SysTenant add = MapstructUtils.convert(bo, SysTenant.class);
        if (StringUtils.isNotBlank(bo.getDbHost()) && StringUtils.isNotBlank(bo.getDbName())) {
            add.setDbUrl(assembleDbUrl(bo.getDbHost(), bo.getDbPort(), bo.getDbName()));
        }
        List<String> tenantIds = baseMapper.selectList(
                new LambdaQueryWrapper<SysTenant>().select(SysTenant::getTenantId))
            .stream().map(SysTenant::getTenantId).toList();
        add.setTenantId(generateTenantId(tenantIds));
        boolean flag = baseMapper.insert(add) > 0;
        if (!flag) {
            throw new ServiceException("创建租户失败");
        }
        bo.setId(add.getId());
        if (bo.getAdminUserId() != null) {
            bindUser(bo.getAdminUserId(), add.getTenantId());
        }
        if (StringUtils.isNotBlank(add.getDbUrl())) {
            registerDatasource(add.getTenantId(), add.getDbUrl(), add.getDbUsername(), add.getDbPassword(), add.getDbDriver());
        }
        return true;
    }

    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
    @Override
    public Boolean updateByBo(SysTenantBo bo) {
        SysTenant old = baseMapper.selectById(bo.getId());
        SysTenant tenant = MapstructUtils.convert(bo, SysTenant.class);
        tenant.setTenantId(null);
        tenant.setPackageId(null);
        if (StringUtils.isNotBlank(bo.getDbHost()) && StringUtils.isNotBlank(bo.getDbName())) {
            tenant.setDbUrl(assembleDbUrl(bo.getDbHost(), bo.getDbPort(), bo.getDbName()));
        }
        boolean updated = baseMapper.updateById(tenant) > 0;
        if (updated && old != null && StringUtils.isNotBlank(old.getTenantId())) {
            boolean dsChanged = !Objects.equals(old.getDbUrl(), tenant.getDbUrl())
                || !Objects.equals(old.getDbUsername(), tenant.getDbUsername())
                || !Objects.equals(old.getDbPassword(), tenant.getDbPassword());
            if (dsChanged && StringUtils.isNotBlank(tenant.getDbUrl())) {
                registerDatasource(old.getTenantId(), tenant.getDbUrl(), tenant.getDbUsername(), tenant.getDbPassword(), tenant.getDbDriver());
            }
        }
        return updated;
    }

    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#bo.tenantId")
    @Override
    public int updateTenantStatus(SysTenantBo bo) {
        SysTenant tenant = new SysTenant();
        tenant.setId(bo.getId());
        tenant.setStatus(bo.getStatus());
        return baseMapper.updateById(tenant);
    }

    @Override
    public void checkTenantAllowed(String tenantId) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            throw new ServiceException("不允许操作管理租户");
        }
    }

    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, allEntries = true)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            if (ids.contains(TenantConstants.SUPER_ADMIN_ID)) {
                throw new ServiceException("超管租户不能删除");
            }
        }
        for (Long id : ids) {
            SysTenant tenant = baseMapper.selectById(id);
            if (tenant != null && StringUtils.isNotBlank(tenant.getTenantId())) {
                tenantUserMapper.delete(new LambdaQueryWrapper<SysTenantUser>()
                    .eq(SysTenantUser::getTenantId, tenant.getTenantId()));
                removeDatasource(tenant.getTenantId());
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public boolean checkCompanyNameUnique(SysTenantBo bo) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysTenant>()
            .eq(SysTenant::getCompanyName, bo.getCompanyName())
            .ne(ObjectUtil.isNotNull(bo.getTenantId()), SysTenant::getTenantId, bo.getTenantId()));
        return !exist;
    }

    @Override
    public boolean checkAccountBalance(String tenantId) {
        SysTenantVo tenant = SpringUtils.getAopProxy(this).queryByTenantId(tenantId);
        if (tenant == null) {
            return false;
        }
        if (tenant.getAccountCount() == -1) {
            return true;
        }
        long userCount = tenantUserMapper.selectCount(
            new LambdaQueryWrapper<SysTenantUser>().eq(SysTenantUser::getTenantId, tenantId));
        return tenant.getAccountCount() - userCount > 0;
    }

    @Override
    public boolean checkExpireTime(String tenantId) {
        SysTenantVo tenant = SpringUtils.getAopProxy(this).queryByTenantId(tenantId);
        if (tenant == null) {
            return false;
        }
        if (ObjectUtil.isNull(tenant.getExpireTime())) {
            return true;
        }
        return new Date().before(tenant.getExpireTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncTenantPackage(String tenantId, Long packageId) {
        SysTenantPackage tenantPackage = tenantPackageMapper.selectById(packageId);
        if (tenantPackage == null) {
            throw new ServiceException("套餐不存在");
        }
        SysTenant tenant = new SysTenant();
        tenant.setTenantId(tenantId);
        tenant.setPackageId(packageId);
        baseMapper.update(tenant, new LambdaQueryWrapper<SysTenant>().eq(SysTenant::getTenantId, tenantId));
        return true;
    }

    @Override
    public boolean testConnection(DbConnectionBo bo) {
        String url = "jdbc:mysql://" + bo.getDbHost() + ":" + bo.getDbPort()
            + "/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8";
        try (Connection conn = DriverManager.getConnection(url, bo.getDbUsername(), bo.getDbPassword())) {
            return conn.isValid(5);
        } catch (Exception e) {
            throw new ServiceException("数据库连接失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> listDatabases(DbConnectionBo bo) {
        String url = "jdbc:mysql://" + bo.getDbHost() + ":" + bo.getDbPort()
            + "/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8";
        Set<String> systemDbs = Set.of("information_schema", "mysql", "performance_schema", "sys");
        try (Connection conn = DriverManager.getConnection(url, bo.getDbUsername(), bo.getDbPassword())) {
            var rs = conn.getMetaData().getCatalogs();
            List<String> databases = new ArrayList<>();
            while (rs.next()) {
                String dbName = rs.getString(1);
                if (!systemDbs.contains(dbName.toLowerCase())) {
                    databases.add(dbName);
                }
            }
            return databases;
        } catch (Exception e) {
            throw new ServiceException("获取数据库列表失败: " + e.getMessage());
        }
    }

    @Override
    public boolean createDatabase(CreateDatabaseBo bo) {
        String url = "jdbc:mysql://" + bo.getDbHost() + ":" + bo.getDbPort()
            + "/?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8";
        try (Connection conn = DriverManager.getConnection(url, bo.getDbUsername(), bo.getDbPassword())) {
            conn.createStatement().execute(
                "CREATE DATABASE IF NOT EXISTS `" + bo.getDbName()
                    + "` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci");
            if (Boolean.TRUE.equals(bo.getInitSchema())) {
                var resource = new ClassPathResource("sql/tenant_db_schema.sql");
                if (resource.exists()) {
                    conn.setCatalog(bo.getDbName());
                    ScriptUtils.executeSqlScript(conn, new EncodedResource(resource, StandardCharsets.UTF_8));
                } else {
                    log.warn("租户数据库初始化脚本不存在: sql/tenant_db_schema.sql，跳过表结构初始化");
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("创建数据库失败: " + e.getMessage());
        }
    }

    @Override
    public boolean bindUser(Long userId, String tenantId) {
        SysTenantUser existing = tenantUserMapper.selectByUserIdAndTenantId(userId, tenantId);
        if (existing != null) {
            return true;
        }
        SysTenantUser binding = new SysTenantUser();
        binding.setUserId(userId);
        binding.setTenantId(tenantId);
        binding.setCreateTime(new Date());
        return tenantUserMapper.insert(binding) > 0;
    }

    @Override
    public boolean unbindUser(Long userId, String tenantId) {
        long count = tenantUserMapper.selectCount(
            new LambdaQueryWrapper<SysTenantUser>().eq(SysTenantUser::getUserId, userId));
        if (count <= 1) {
            throw new ServiceException("不能解绑最后一个租户");
        }
        return tenantUserMapper.delete(
            new LambdaQueryWrapper<SysTenantUser>()
                .eq(SysTenantUser::getUserId, userId)
                .eq(SysTenantUser::getTenantId, tenantId)) > 0;
    }

    @Override
    public List<SysUserVo> getUsersByTenant(String tenantId) {
        return TenantHelper.ignore(() -> {
            List<SysTenantUser> bindings = tenantUserMapper.selectList(
                new LambdaQueryWrapper<SysTenantUser>().eq(SysTenantUser::getTenantId, tenantId));
            if (bindings.isEmpty()) {
                return List.of();
            }
            List<Long> userIds = bindings.stream().map(SysTenantUser::getUserId).toList();
            return userMapper.selectVoByIds(userIds);
        });
    }

    @Override
    public List<SysTenantVo> getTenantsByUser(Long userId) {
        return TenantHelper.ignore(() -> {
            List<SysTenantUser> bindings = tenantUserMapper.selectList(
                new LambdaQueryWrapper<SysTenantUser>().eq(SysTenantUser::getUserId, userId));
            if (bindings.isEmpty()) {
                return List.of();
            }
            List<String> tenantIds = bindings.stream().map(SysTenantUser::getTenantId).toList();
            List<SysTenantVo> list = baseMapper.selectVoList(
                new LambdaQueryWrapper<SysTenant>().in(SysTenant::getTenantId, tenantIds));
            list.forEach(this::fillDbParts);
            return list;
        });
    }

    // ---- private helpers ----

    private String generateTenantId(List<String> tenantIds) {
        String numbers = RandomUtil.randomNumbers(6);
        if (tenantIds.contains(numbers)) {
            return generateTenantId(tenantIds);
        }
        return numbers;
    }

    private String assembleDbUrl(String host, Integer port, String dbName) {
        return "jdbc:mysql://" + host + ":" + (port != null ? port : 3306)
            + "/" + dbName + "?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8";
    }

    private void fillDbParts(SysTenantVo vo) {
        if (vo == null || StringUtils.isBlank(vo.getDbUrl())) {
            return;
        }
        try {
            URI uri = new URI(vo.getDbUrl().substring(5)); // strip "jdbc:"
            vo.setDbHost(uri.getHost());
            vo.setDbPort(uri.getPort());
            String path = uri.getPath();
            if (StringUtils.isNotBlank(path) && path.startsWith("/")) {
                vo.setDbName(path.substring(1));
                int queryIdx = vo.getDbName().indexOf('?');
                if (queryIdx > 0) {
                    vo.setDbName(vo.getDbName().substring(0, queryIdx));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void registerDatasource(String tenantId, String dbUrl, String dbUsername, String dbPassword, String dbDriver) {
        DynamicRoutingDataSource dynamicDs = (DynamicRoutingDataSource) SpringUtils.getBean(DataSource.class);
        String dsName = "tenant_" + tenantId;
        if (dynamicDs.getDataSources().containsKey(dsName)) {
            removeDatasource(tenantId);
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        ds.setDriverClassName(StringUtils.isNotBlank(dbDriver) ? dbDriver : "com.mysql.cj.jdbc.Driver");
        ds.setMaximumPoolSize(20);
        ds.setMinimumIdle(5);
        ds.setConnectionTimeout(30000);
        ds.setIdleTimeout(300000);
        ds.setMaxLifetime(1800000);
        ds.setConnectionTestQuery("SELECT 1");
        dynamicDs.addDataSource(dsName, ds);
        log.info("注册租户数据源: {} -> {}", dsName, dbUrl);
    }

    private void removeDatasource(String tenantId) {
        DynamicRoutingDataSource dynamicDs = (DynamicRoutingDataSource) SpringUtils.getBean(DataSource.class);
        String dsName = "tenant_" + tenantId;
        DataSource ds = dynamicDs.getDataSources().remove(dsName);
        if (ds instanceof HikariDataSource hikari) {
            hikari.close();
        }
        log.info("销毁租户数据源: {}", dsName);
    }
}
