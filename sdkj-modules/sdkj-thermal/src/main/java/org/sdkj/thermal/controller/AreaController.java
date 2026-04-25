package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.domain.Area;
import org.sdkj.thermal.service.IAreaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/area")
public class AreaController extends BaseController {

    private final IAreaService areaService;

    /**
     * 获取省份列表
     */
    @SaCheckLogin
    @GetMapping("/provinces")
    public R<List<Area>> provinceList() {
        return R.ok(areaService.getProvinces());
    }

    /**
     * 根据省份ID获取城市列表
     */
    @SaCheckLogin
    @GetMapping("/cities/{provinceId}")
    public R<List<Area>> cityList(@PathVariable String provinceId) {
        return R.ok(areaService.getCities(provinceId));
    }

    /**
     * 根据城市ID获取区县列表
     */
    @SaCheckLogin
    @GetMapping("/districts/{cityId}")
    public R<List<Area>> districtList(@PathVariable String cityId) {
        return R.ok(areaService.getDistricts(cityId));
    }
}
