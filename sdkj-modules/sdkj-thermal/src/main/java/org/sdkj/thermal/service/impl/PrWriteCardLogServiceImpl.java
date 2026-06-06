package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.system.domain.SysUser;
import org.sdkj.system.domain.vo.SysUserVo;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.domain.bo.PrWriteCardLogBo;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;
import org.sdkj.thermal.service.IPrWriteCardLogService;
import org.sdkj.thermal.service.OrgAccessService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PrWriteCardLogServiceImpl implements IPrWriteCardLogService {

    private final PrValveOperationLogMapper baseMapper;
    private final PrHouseMapper houseMapper;
    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrCompanyMapper companyMapper;
    private final SysUserMapper sysUserMapper;
    private final OrgAccessService orgAccessService;

    @Override
    public Long insertWriteCardLog(PrWriteCardLogBo bo) {
        orgAccessService.assertCurrentUserCanAccessOrg(bo.getOrgId());
        PrValveOperationLog entity = new PrValveOperationLog();
        entity.setMeterId(bo.getMeterId());
        entity.setMeterNum(bo.getMeterNum());
        entity.setCardNum(bo.getCardNum());
        entity.setType(bo.getType());
        entity.setCardType(StringUtils.isBlank(bo.getCardType()) ? "1" : bo.getCardType());
        entity.setContent(bo.getContent());
        entity.setOrgId(bo.getOrgId());
        entity.setValveStatus(bo.getValveStatus());
        entity.setOperatorId(String.valueOf(LoginHelper.getUserId()));
        entity.setOperationTime(new Date());
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public TableDataInfo<PrValveOperationLogVo> selectPageList(PrWriteCardLogBo bo, PageQuery pageQuery) {
        // 楼宇/单元两跳筛选：pr_house(按 org/building/unit) → house_id → valve_archive(按 house_id) → meter_id
        List<Long> meterIds = resolveMeterIds(bo.getOrgId(), bo.getBuildingId(), bo.getUnitCode());
        if (meterIds != null && meterIds.isEmpty()) {
            return TableDataInfo.build(new java.util.ArrayList<>());
        }

        LambdaQueryWrapper<PrValveOperationLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(bo.getOrgId()), PrValveOperationLog::getOrgId, bo.getOrgId());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), PrValveOperationLog::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getOperatorId()), PrValveOperationLog::getOperatorId, bo.getOperatorId());
        lqw.in(meterIds != null, PrValveOperationLog::getMeterId, meterIds);
        lqw.ge(bo.getStartTime() != null, PrValveOperationLog::getOperationTime, bo.getStartTime());
        lqw.le(bo.getEndTime() != null, PrValveOperationLog::getOperationTime, bo.getEndTime());
        lqw.orderByDesc(PrValveOperationLog::getOperationTime);

        Page<PrValveOperationLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        enrich(result.getRecords());
        return TableDataInfo.build(result);
    }

    /**
     * 楼宇/单元 → meter_id 集合（两跳无 JOIN）。
     * null = 无楼宇/单元筛选（调用方跳过 IN）；非 null 空 = 命中 0 → 列表空。
     */
    private List<Long> resolveMeterIds(String orgId, String buildingId, String unitCode) {
        boolean hasFilter = StringUtils.isNotBlank(buildingId) || StringUtils.isNotBlank(unitCode);
        if (!hasFilter) return null;
        // 跳1：pr_house → house_id
        LambdaQueryWrapper<PrHouse> hq = new LambdaQueryWrapper<>();
        hq.eq(StringUtils.isNotBlank(orgId), PrHouse::getOrgId, orgId)
          .eq(StringUtils.isNotBlank(buildingId), PrHouse::getBuildingId, buildingId)
          .eq(StringUtils.isNotBlank(unitCode), PrHouse::getUnitCode, unitCode)
          .select(PrHouse::getId);
        List<Long> houseIds = houseMapper.selectList(hq).stream().map(PrHouse::getId).toList();
        if (houseIds.isEmpty()) return Collections.emptyList();
        // 跳2：pr_heat_valve_archive(house_id in) → id(meter_id)
        LambdaQueryWrapper<PrHeatValveArchive> vq = new LambdaQueryWrapper<>();
        vq.in(PrHeatValveArchive::getHouseId, houseIds).select(PrHeatValveArchive::getId);
        return valveArchiveMapper.selectList(vq).stream().map(PrHeatValveArchive::getId).toList();
    }

    /** 回填 orgName + operatorName（两步无 JOIN，复用阶段5 同模式）。 */
    private void enrich(List<PrValveOperationLogVo> rows) {
        if (rows == null || rows.isEmpty()) return;
        Map<String, String> orgNameMap = new HashMap<>();
        rows.stream().map(PrValveOperationLogVo::getOrgId)
            .filter(StringUtils::isNotBlank).distinct()
            .forEach(oid -> {
                SysOrganization org = companyMapper.selectOrgById(oid);
                if (org != null) orgNameMap.put(oid, org.getName());
            });
        Set<Long> userIds = rows.stream()
            .map(PrValveOperationLogVo::getOperatorId)
            .map(PrWriteCardLogServiceImpl::parseLongOrNull)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> userNameMap = userIds.isEmpty() ? Collections.emptyMap()
            : sysUserMapper.selectVoList(new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getUserId, userIds).select(SysUser::getUserId, SysUser::getNickName))
                .stream().collect(Collectors.toMap(SysUserVo::getUserId, SysUserVo::getNickName, (a, b) -> a));
        for (PrValveOperationLogVo v : rows) {
            v.setOrgName(orgNameMap.get(v.getOrgId()));
            Long uid = parseLongOrNull(v.getOperatorId());
            if (uid != null) v.setOperatorName(userNameMap.get(uid));
        }
    }

    private static Long parseLongOrNull(String val) {
        if (StringUtils.isBlank(val)) return null;
        try { return Long.valueOf(val.trim()); } catch (NumberFormatException e) { return null; }
    }
}
