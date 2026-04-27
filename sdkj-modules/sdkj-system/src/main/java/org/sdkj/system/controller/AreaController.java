package org.sdkj.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.system.domain.Area;
import org.sdkj.system.service.IAreaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 省市区三级联动控制器
 * <p>
 * 提供省/市/区县三级级联数据查询接口，无需登录即可访问。
 * 数据来源：sys_area 表。
 * </p>
 *
 * @author sdkj
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/area")
public class AreaController extends BaseController {

    private final IAreaService areaService;

    /**
     * 获取省份列表
     *
     * @return 省份列表
     */
    @SaIgnore
    @GetMapping("/provinces")
    public R<List<Area>> provinces() {
        return R.ok(areaService.getProvinces());
    }

    /**
     * 根据省份ID获取城市列表
     *
     * @param provinceId 省份ID
     * @return 城市列表
     */
    @SaIgnore
    @GetMapping("/cities")
    public R<List<Area>> cities(@RequestParam String provinceId) {
        return R.ok(areaService.getCities(provinceId));
    }

    /**
     * 根据城市ID获取区县列表
     *
     * @param cityId 城市ID
     * @return 区县列表
     */
    @SaIgnore
    @GetMapping("/counties")
    public R<List<Area>> counties(@RequestParam String cityId) {
        return R.ok(areaService.getCounties(cityId));
    }

}
