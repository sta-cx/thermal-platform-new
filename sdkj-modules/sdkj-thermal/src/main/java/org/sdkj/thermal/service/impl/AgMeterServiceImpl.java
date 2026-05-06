package org.sdkj.thermal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.common.mybatis.utils.IdGeneratorUtil;
import org.sdkj.thermal.mapper.AgMeterMapper;
import org.sdkj.thermal.service.IAgMeterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 代理商仪表分配 Service 实现
 * 迁移自旧系统 AgentMeterServiceImpl
 */
@Service
@RequiredArgsConstructor
public class AgMeterServiceImpl implements IAgMeterService {

    private final AgMeterMapper agMeterMapper;

    @Override
    public TableDataInfo<Map<String, Object>> queryAllocatedMeters(
            String companyId, String meterType, String search, PageQuery pageQuery) {
        Page<Map<String, Object>> page = pageQuery.build();
        Page<Map<String, Object>> result;
        switch (meterType) {
            case "01" -> result = agMeterMapper.getAllocatedElectricList(page, companyId, search);
            case "02" -> result = agMeterMapper.getAllocatedWaterList(page, companyId, search);
            case "03" -> result = agMeterMapper.getAllocatedHeatList(page, companyId, search);
            case "04" -> result = agMeterMapper.getAllocatedGasList(page, companyId, search);
            default -> throw new ServiceException("不支持的仪表类型: " + meterType);
        }
        return TableDataInfo.build(result);
    }

    @Override
    public List<Map<String, Object>> queryAllMeters(String meterType, String search) {
        return switch (meterType) {
            case "01" -> agMeterMapper.getAllElectricList(search);
            case "02" -> agMeterMapper.getAllWaterList(search);
            case "03" -> agMeterMapper.getAllHeatList(search);
            case "04" -> agMeterMapper.getAllGasList(search);
            default -> throw new ServiceException("不支持的仪表类型: " + meterType);
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allocateMeters(String companyId, String archiveIds, String meterType) {
        // 1. 删除该公司该类型的所有旧分配记录
        agMeterMapper.removeMeterMatch(companyId, meterType);

        // 2. 如果archiveIds为空，说明是清空操作，直接返回
        if (StringUtils.isBlank(archiveIds)) {
            return;
        }

        // 3. 解析逗号分隔的档案ID列表
        List<Long> ids = Arrays.stream(archiveIds.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::valueOf)
            .toList();

        if (ids.isEmpty()) {
            return;
        }

        // 4. 批量插入新的分配记录
        String username = StpUtil.getLoginIdAsString();
        List<Long> matchIds = ids.stream().map(i -> IdGeneratorUtil.nextLongId()).toList();
        agMeterMapper.insertMeterMatch(companyId, ids, matchIds, meterType, username);
    }
}
