package org.sdkj.thermal.service.support;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sdkj.thermal.service.OrgAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Base service for org-scoped business tables.
 */
public abstract class OrgScopedServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Autowired
    private OrgAccessService orgAccessService;

    @Override
    public boolean save(T entity) {
        assertEntityOrgAllowed(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(T entity) {
        assertEntityOrgAllowed(entity);
        return super.updateById(entity);
    }

    @Override
    public boolean update(T entity, Wrapper<T> updateWrapper) {
        assertEntityOrgAllowed(entity);
        return super.update(entity, updateWrapper);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        assertEntityOrgAllowed(entity);
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        assertEntitiesOrgAllowed(entityList);
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        assertEntitiesOrgAllowed(entityList);
        return super.updateBatchById(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        assertEntitiesOrgAllowed(entityList);
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    protected void assertOrgAllowed(String orgId) {
        if (StringUtils.hasText(orgId)) {
            orgAccessService.assertCurrentUserCanAccessOrg(orgId);
        }
    }

    protected void assertOrgAllowed(Collection<String> orgIds) {
        if (orgIds == null || orgIds.isEmpty()) {
            return;
        }
        List<String> normalized = orgIds.stream()
            .filter(StringUtils::hasText)
            .toList();
        if (!normalized.isEmpty()) {
            orgAccessService.assertCurrentUserCanAccessOrgIds(normalized);
        }
    }

    protected void assertEntityOrgAllowed(Object entity) {
        assertOrgAllowed(readOrgId(entity));
    }

    protected void assertEntitiesOrgAllowed(Collection<?> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        assertOrgAllowed(entities.stream()
            .map(this::readOrgId)
            .filter(Objects::nonNull)
            .toList());
    }

    private String readOrgId(Object entity) {
        if (entity == null) {
            return null;
        }
        try {
            Method method = entity.getClass().getMethod("getOrgId");
            Object value = method.invoke(entity);
            return value == null ? null : String.valueOf(value);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
