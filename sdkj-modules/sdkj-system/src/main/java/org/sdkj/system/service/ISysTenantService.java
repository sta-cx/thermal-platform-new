package org.sdkj.system.service;

import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.system.domain.bo.CreateDatabaseBo;
import org.sdkj.system.domain.bo.DbConnectionBo;
import org.sdkj.system.domain.bo.SysTenantBo;
import org.sdkj.system.domain.vo.SysTenantVo;
import org.sdkj.system.domain.vo.SysUserVo;

import java.util.Collection;
import java.util.List;

public interface ISysTenantService {

    SysTenantVo queryById(Long id);

    SysTenantVo queryByTenantId(String tenantId);

    TableDataInfo<SysTenantVo> queryPageList(SysTenantBo bo, PageQuery pageQuery);

    List<SysTenantVo> queryList(SysTenantBo bo);

    Boolean insertByBo(SysTenantBo bo);

    Boolean updateByBo(SysTenantBo bo);

    int updateTenantStatus(SysTenantBo bo);

    void checkTenantAllowed(String tenantId);

    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    boolean checkCompanyNameUnique(SysTenantBo bo);

    boolean checkAccountBalance(String tenantId);

    boolean checkExpireTime(String tenantId);

    Boolean syncTenantPackage(String tenantId, Long packageId);

    boolean testConnection(DbConnectionBo bo);

    List<String> listDatabases(DbConnectionBo bo);

    boolean createDatabase(CreateDatabaseBo bo);

    boolean bindUser(Long userId, String tenantId);

    boolean unbindUser(Long userId, String tenantId);

    List<SysUserVo> getUsersByTenant(String tenantId);

    List<SysTenantVo> getTenantsByUser(Long userId);
}
