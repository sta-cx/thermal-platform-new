package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.web.core.BaseController;
import org.sdkj.thermal.service.IPrWechatAuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wechat/wx-user")
public class WxUserListController extends BaseController {

    private final IPrWechatAuthService wechatAuthService;

    @SaCheckLogin
    @GetMapping("/list")
    public R<?> list() {
        return R.ok(wechatAuthService.list());
    }
}
