package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/area")
public class AreaController extends BaseController {

    @SaCheckLogin
    @GetMapping("/province")
    public R<List<Map<String, Object>>> provinceList() {
        return R.ok(new ArrayList<>());
    }

    @SaCheckLogin
    @GetMapping("/city/{provinceCode}")
    public R<List<Map<String, Object>>> cityList(@PathVariable String provinceCode) {
        return R.ok(new ArrayList<>());
    }

    @SaCheckLogin
    @GetMapping("/district/{cityCode}")
    public R<List<Map<String, Object>>> districtList(@PathVariable String cityCode) {
        return R.ok(new ArrayList<>());
    }

    @SaCheckLogin
    @GetMapping("/street/{districtCode}")
    public R<List<Map<String, Object>>> streetList(@PathVariable String districtCode) {
        return R.ok(new ArrayList<>());
    }
}
