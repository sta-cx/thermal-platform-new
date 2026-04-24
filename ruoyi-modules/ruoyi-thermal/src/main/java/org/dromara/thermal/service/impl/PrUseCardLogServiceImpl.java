package org.dromara.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.thermal.domain.PrUseCardLog;
import org.dromara.thermal.domain.vo.PrUseCardLogVo;
import org.dromara.thermal.mapper.PrUseCardLogMapper;
import org.dromara.thermal.service.IPrUseCardLogService;
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
