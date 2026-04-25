package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.thermal.domain.Area;

import java.util.List;

/**
 * 省市区Service接口
 */
public interface IAreaService extends IService<Area> {

    /**
     * 获取省份列表
     */
    List<Area> getProvinces();

    /**
     * 根据省份ID获取城市列表
     */
    List<Area> getCities(String provinceId);

    /**
     * 根据城市ID获取区县列表
     */
    List<Area> getDistricts(String cityId);
}
