package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrUseCardLog;
import org.sdkj.thermal.domain.vo.PrUseCardLogVo;
import org.sdkj.thermal.mapper.PrUseCardLogMapper;
import org.sdkj.thermal.service.IPrUseCardLogService;
import org.springframework.stereotype.Service;

/**
 * 写卡日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class PrUseCardLogServiceImpl extends ServiceImpl<PrUseCardLogMapper, PrUseCardLog>
        implements IPrUseCardLogService {

    private final PrUseCardLogMapper baseMapper;

    @Override
    public TableDataInfo<PrUseCardLogVo> selectPageList(LambdaQueryWrapper<PrUseCardLog> lqw, PageQuery pageQuery) {
        Page<PrUseCardLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    @Override
    public boolean changeValveStatus(String meterId, Integer valveStatus) {
        // TODO: 实际阀门状态变更需要通过 MBus 通信
        PrUseCardLog log = new PrUseCardLog();
        log.setMeterId(meterId);
        log.setValveStatus(valveStatus);
        return save(log);
    }
}
