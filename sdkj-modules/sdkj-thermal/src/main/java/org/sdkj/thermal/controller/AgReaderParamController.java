package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.thermal.domain.AgReaderParam;
import org.sdkj.thermal.service.IAgReaderParamService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 自助机读卡器参数管理
 * 迁移自旧系统 AgReaderParamController
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/agent/reader-param")
public class AgReaderParamController {

    private final IAgReaderParamService service;

    @SaCheckPermission("thermal:agent:reader:query")
    @SaCheckLogin
    @GetMapping
    public R<AgReaderParam> getByCode(@RequestParam String code) {
        AgReaderParam param = service.getOne(
            new LambdaQueryWrapper<AgReaderParam>().eq(AgReaderParam::getCode, code));
        return param != null ? R.ok(param) : R.fail("未找到参数配置");
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(service.list());
    }
}
