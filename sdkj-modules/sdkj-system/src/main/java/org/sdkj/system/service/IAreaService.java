package org.sdkj.system.service;

import org.sdkj.system.domain.Area;

import java.util.List;

/**
 * 省市区Service接口
 *
 * @author sdkj
 */
public interface IAreaService {

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
    List<Area> getCounties(String cityId);

}
