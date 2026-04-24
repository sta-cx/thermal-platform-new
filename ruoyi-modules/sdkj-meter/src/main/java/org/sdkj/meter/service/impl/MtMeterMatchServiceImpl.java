package org.sdkj.meter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.meter.domain.MtMeterMatch;
import org.sdkj.meter.mapper.MtMeterMatchMapper;
import org.sdkj.meter.service.IMtMeterMatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仪表分配服务实现
 */
@Service
@RequiredArgsConstructor
public class MtMeterMatchServiceImpl extends ServiceImpl<MtMeterMatchMapper, MtMeterMatch> implements IMtMeterMatchService {

    private final MtMeterMatchMapper baseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAllocate(String companyId, List<String> archiveIds, String meterType) {
        // 删除该公司该类型的所有分配
        baseMapper.delete(
            new LambdaQueryWrapper<MtMeterMatch>()
                .eq(MtMeterMatch::getCompanyId, companyId)
                .eq(MtMeterMatch::getMeterType, meterType)
        );
        // 批量新增分配
        for (String archiveId : archiveIds) {
            if (archiveId == null || archiveId.isBlank()) continue;
            MtMeterMatch match = new MtMeterMatch();
            match.setCompanyId(companyId);
            match.setArchiveId(archiveId.trim());
            match.setMeterType(meterType);
            baseMapper.insert(match);
        }
    }
}
