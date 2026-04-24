package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.HtTaskSettingLog;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogItemVo;
import org.sdkj.thermal.domain.vo.HtTaskSettingLogVo;
import org.sdkj.thermal.mapper.HtTaskSettingLogMapper;
import org.sdkj.thermal.service.IHtTaskSettingLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 调控设定日志服务实现
 */
@Service
@RequiredArgsConstructor
public class HtTaskSettingLogServiceImpl extends ServiceImpl<HtTaskSettingLogMapper, HtTaskSettingLog> implements IHtTaskSettingLogService {

    private final HtTaskSettingLogMapper baseMapper;

    @Override
    public TableDataInfo<HtTaskSettingLogVo> selectPageList(LambdaQueryWrapper<HtTaskSettingLog> lqw, PageQuery pageQuery) {
        Page<HtTaskSettingLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public List<HtTaskSettingLogItemVo> selectItemsByMainId(String mainId) {
        return baseMapper.selectItemsByMainId(mainId);
    }
}
