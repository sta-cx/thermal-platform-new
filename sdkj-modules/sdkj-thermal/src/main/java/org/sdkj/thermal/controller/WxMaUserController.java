package org.sdkj.thermal.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/user/{appid}")
public class WxMaUserController {

    private final WxMaService wxMaService;

    @SaIgnore
    @GetMapping("/login")
    public R<Map<String, Object>> login(@PathVariable String appid, @RequestParam String code) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("未找到对应appid=[" + appid + "]的配置");
        }
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            Map<String, Object> result = new HashMap<>();
            result.put("openid", session.getOpenid());
            result.put("sessionKey", session.getSessionKey());
            result.put("unionid", session.getUnionid());
            return R.ok(result);
        } catch (WxErrorException e) {
            log.error("小程序登录失败: appid={}, code={}", appid, code, e);
            return R.fail("登录失败: " + e.getMessage());
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    @SaIgnore
    @GetMapping("/phone")
    public R<Map<String, Object>> getPhone(@PathVariable String appid, @RequestParam String code) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("未找到对应appid=[" + appid + "]的配置");
        }
        try {
            WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService().getPhoneNoInfo(code);
            Map<String, Object> result = new HashMap<>();
            result.put("phoneNumber", phoneInfo.getPhoneNumber());
            result.put("purePhoneNumber", phoneInfo.getPurePhoneNumber());
            result.put("countryCode", phoneInfo.getCountryCode());
            return R.ok(result);
        } catch (WxErrorException e) {
            log.error("获取手机号失败: appid={}", appid, e);
            return R.fail("获取手机号失败: " + e.getMessage());
        } finally {
            WxMaConfigHolder.remove();
        }
    }
}
