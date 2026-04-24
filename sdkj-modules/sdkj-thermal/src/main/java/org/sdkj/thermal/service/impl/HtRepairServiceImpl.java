package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtRepair;
import org.sdkj.thermal.domain.vo.HtRepairVo;
import org.sdkj.thermal.mapper.HtRepairMapper;
import org.sdkj.thermal.service.IHtRepairService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 报修记录 Service 实现
 * 迁移自旧系统 HtRepairServiceImpl
 */
@Service
@RequiredArgsConstructor
public class HtRepairServiceImpl extends ServiceImpl<HtRepairMapper, HtRepair> implements IHtRepairService {

    private final HtRepairMapper baseMapper;

    @Override
    public TableDataInfo<HtRepairVo> selectPageList(LambdaQueryWrapper<HtRepair> lqw, PageQuery pageQuery) {
        Page<HtRepairVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<Map<String, Object>> selectTypeCount(String companyId) {
        return baseMapper.selectTypeCount(companyId);
    }

    @Override
    public List<HtRepairVo> selectByRoomId(String roomId) {
        return baseMapper.selectByRoomId(roomId);
    }

    @Override
    public int markAsDeleted(String repairNo, String companyId) {
        return baseMapper.markAsDeleted(repairNo, companyId);
    }

}
