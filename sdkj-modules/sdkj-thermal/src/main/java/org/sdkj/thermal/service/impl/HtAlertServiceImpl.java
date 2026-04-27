package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtAlert;
import org.sdkj.thermal.domain.vo.HtAlertVo;
import org.sdkj.thermal.mapper.HtAlertMapper;
import org.sdkj.thermal.service.IHtAlertService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 报警记录 Service 实现
 * 迁移自旧系统 HtAlertServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtAlertServiceImpl extends ServiceImpl<HtAlertMapper, HtAlert> implements IHtAlertService {

    private final HtAlertMapper baseMapper;

    @Override
    public TableDataInfo<HtAlertVo> selectPageList(LambdaQueryWrapper<HtAlert> lqw, PageQuery pageQuery) {
        Page<HtAlertVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtAlertVo> selectAbnormalAlarmList(String meterId) {
        return baseMapper.selectAbnormalAlarmList(meterId);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount(String companyId) {
        return baseMapper.selectTypeCount(companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchInsertAlerts(List<HtAlert> list) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        return saveBatch(list);
    }

    @Override
    public List<Map<String, Object>> selectTypeCountDtu(String companyId) {
        return baseMapper.selectTypeCountDtu(companyId);
    }

}
