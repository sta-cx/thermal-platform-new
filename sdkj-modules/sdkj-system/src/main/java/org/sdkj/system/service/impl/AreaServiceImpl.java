package org.sdkj.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.constant.SystemConstants;
import org.sdkj.system.domain.Area;
import org.sdkj.system.mapper.AreaMapper;
import org.sdkj.system.service.IAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 省市区Service实现
 *
 * @author sdkj
 */
@Service("systemAreaServiceImpl")
@RequiredArgsConstructor
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements IAreaService {

    /**
     * 获取省份列表（parentId = '1' 表示顶级节点）
     */
    @Override
    public List<Area> getProvinces() {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, SystemConstants.ROOT_DEPT_ANCESTORS)
            .eq(Area::getDelFlag, SystemConstants.NORMAL)
            .orderByAsc(Area::getSort));
    }

    /**
     * 根据省份ID获取城市列表
     */
    @Override
    public List<Area> getCities(String provinceId) {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, provinceId)
            .eq(Area::getDelFlag, SystemConstants.NORMAL)
            .orderByAsc(Area::getSort));
    }

    /**
     * 根据城市ID获取区县列表
     */
    @Override
    public List<Area> getCounties(String cityId) {
        return list(new LambdaQueryWrapper<Area>()
            .eq(Area::getParentId, cityId)
            .eq(Area::getDelFlag, SystemConstants.NORMAL)
            .orderByAsc(Area::getSort));
    }

}
