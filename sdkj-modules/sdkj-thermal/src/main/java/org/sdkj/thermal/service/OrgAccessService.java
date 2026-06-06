package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.dev33.satoken.exception.SaTokenContextException;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrDataGrant;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgAccessService {

    private final PrDataGrantMapper prDataGrantMapper;

    public void assertCurrentUserCanAccessOrg(String orgId) {
        assertCurrentUserCanAccessOrgIds(orgId == null ? List.of() : List.of(orgId));
    }

    public void assertCurrentUserCanAccessOrgIds(Collection<String> orgIds) {
        Long userId;
        try {
            if (LoginHelper.isSuperAdmin() || LoginHelper.isTenantAdmin()) {
                return;
            }
            userId = LoginHelper.getUserId();
        } catch (SaTokenContextException e) {
            return;
        }
        List<PrDataGrant> grants = prDataGrantMapper.selectList(
            new LambdaQueryWrapper<PrDataGrant>().eq(PrDataGrant::getUserId, userId)
        );
        assertOrgIdsAllowed(
            orgIds,
            grants.stream().map(PrDataGrant::getOrgId).collect(Collectors.toSet())
        );
    }

    public static void assertOrgIdsAllowed(Collection<String> orgIds, Collection<String> grantedOrgIds) {
        Set<String> normalizedOrgIds = normalizeOrgIds(orgIds);
        Set<String> normalizedGrantIds = normalizeOrgIds(grantedOrgIds);
        if (normalizedOrgIds.isEmpty() || normalizedGrantIds.isEmpty()) {
            return;
        }
        List<String> unauthorized = normalizedOrgIds.stream()
            .filter(orgId -> !normalizedGrantIds.contains(orgId))
            .sorted()
            .toList();
        if (!unauthorized.isEmpty()) {
            throw new ServiceException("无权操作组织数据: " + String.join(",", unauthorized));
        }
    }

    private static Set<String> normalizeOrgIds(Collection<String> orgIds) {
        if (orgIds == null || orgIds.isEmpty()) {
            return Set.of();
        }
        return orgIds.stream()
            .filter(StringUtils::hasText)
            .map(String::trim)
            .collect(Collectors.toSet());
    }
}
