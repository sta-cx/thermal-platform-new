package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.utils.StringUtils;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.vo.PrValveOperationLogVo;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;
import org.sdkj.thermal.service.IPrValveOperationLogService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrValveOperationLogServiceImpl extends ServiceImpl<PrValveOperationLogMapper, PrValveOperationLog>
        implements IPrValveOperationLogService {

    private final PrValveOperationLogMapper baseMapper;

    @Override
    public TableDataInfo<PrValveOperationLogVo> selectPageList(PageQuery pageQuery, String orgId, String meterNum) {
        LambdaQueryWrapper<PrValveOperationLog> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(orgId), PrValveOperationLog::getOrgId, orgId)
           .like(StringUtils.isNotBlank(meterNum), PrValveOperationLog::getMeterNum, meterNum)
           .isNotNull(PrValveOperationLog::getValveStatus)
           .orderByDesc(PrValveOperationLog::getOperationTime);
        Page<PrValveOperationLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }
}
