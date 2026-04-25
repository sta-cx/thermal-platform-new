package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.mapper.PrHeatStationPartitionMapper;
import org.sdkj.thermal.service.IPrHeatStationPartitionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrHeatStationPartitionServiceImpl extends ServiceImpl<PrHeatStationPartitionMapper, PrHeatStationPartition>
        implements IPrHeatStationPartitionService {

    private final PrHeatStationPartitionMapper baseMapper;

    @Override
    public List<PrHeatStationPartition> selectByCompanyId(String companyId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrHeatStationPartition>()
                .eq(PrHeatStationPartition::getCompanyId, companyId)
                .orderByAsc(PrHeatStationPartition::getSeq));
    }

    @Override
    public List<PrHeatStationPartition> selectByStationId(String stationId) {
        return baseMapper.selectList(
            new LambdaQueryWrapper<PrHeatStationPartition>()
                .eq(PrHeatStationPartition::getStationId, stationId)
                .orderByAsc(PrHeatStationPartition::getSeq));
    }
}
