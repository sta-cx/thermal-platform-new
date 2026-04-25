package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.Area;
import org.sdkj.thermal.mapper.AreaMapper;
import org.sdkj.thermal.service.IAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 省市区Service实现
 */
@Service
@RequiredArgsConstructor
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

    @Override
    public List<Area> getProvinces() {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, "1")
            .eq(Area::getDelFlag, "0")
            .orderByAsc(Area::getSort));
    }

    @Override
    public List<Area> getCities(String provinceId) {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, provinceId)
            .eq(Area::getDelFlag, "0")
            .orderByAsc(Area::getSort));
    }

    @Override
    public List<Area> getDistricts(String cityId) {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, cityId)
            .eq(Area::getDelFlag, "0")
            .orderByAsc(Area::getSort));
    }
}
