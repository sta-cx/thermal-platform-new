package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.system.domain.SysUser;
import org.sdkj.system.domain.vo.SysUserVo;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;
import org.sdkj.thermal.service.IPrValveOperationLogService;
import org.sdkj.thermal.service.support.OrgScopedServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrValveOperationLogServiceImpl extends OrgScopedServiceImpl<PrValveOperationLogMapper, PrValveOperationLog>
        implements IPrValveOperationLogService {

    private final PrValveOperationLogMapper baseMapper;
    private final PrCompanyMapper companyMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public TableDataInfo<PrValveOperationLogVo> selectPageList(PageQuery pageQuery, String orgId, String meterNum) {
        LambdaQueryWrapper<PrValveOperationLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrValveOperationLog::getOrgId, orgId)
           .like(StringUtils.isNotBlank(meterNum), PrValveOperationLog::getMeterNum, meterNum)
           .isNotNull(PrValveOperationLog::getValveStatus)
           .orderByDesc(PrValveOperationLog::getOperationTime);
        Page<PrValveOperationLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        enrich(result.getRecords());
        return TableDataInfo.build(result);
    }

    /** 回填小区名 + 操作人名（两步无 JOIN）。 */
    private void enrich(List<PrValveOperationLogVo> rows) {
        if (rows == null || rows.isEmpty()) return;

        // orgId → orgName
        Map<String, String> orgNameMap = new HashMap<>();
        rows.stream().map(PrValveOperationLogVo::getOrgId)
            .filter(StringUtils::isNotBlank).distinct()
            .forEach(oid -> {
                SysOrganization org = companyMapper.selectOrgById(oid);
                if (org != null) orgNameMap.put(oid, org.getName());
            });

        // operatorId(String) → nickName（sys_user 在 master 库）
        Set<Long> userIds = rows.stream()
            .map(PrValveOperationLogVo::getOperatorId)
            .map(PrValveOperationLogServiceImpl::parseLongOrNull)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, String> userNameMap;
        if (userIds.isEmpty()) {
            userNameMap = Collections.emptyMap();
        } else {
            userNameMap = sysUserMapper.selectVoList(
                new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getUserId, userIds)
                    .select(SysUser::getUserId, SysUser::getNickName)
            ).stream().collect(Collectors.toMap(SysUserVo::getUserId, SysUserVo::getNickName, (a, b) -> a));
        }

        for (PrValveOperationLogVo v : rows) {
            v.setOrgName(orgNameMap.get(v.getOrgId()));
            Long uid = parseLongOrNull(v.getOperatorId());
            if (uid != null) v.setOperatorName(userNameMap.get(uid));
        }
    }

    private static Long parseLongOrNull(String val) {
        if (StringUtils.isBlank(val)) return null;
        try {
            return Long.valueOf(val.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
