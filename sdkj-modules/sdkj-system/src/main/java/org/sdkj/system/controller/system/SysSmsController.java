package org.sdkj.system.controller.system;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.RandomUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.constant.GlobalConstants;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.redis.utils.RedisUtils;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.LinkedHashMap;

/**
 * 短信验证码操作处理
 *
 * @author sdkj
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/sms")
public class SysSmsController {

    /**
     * 短信验证码有效期（分钟）
     */
    private static final int SMS_CODE_TTL_MINUTES = 5;

    /**
     * 发送短信验证码
     * <p>短信频率限制由 sms4j 配置控制（restricted: true, minute-max: 1）
     *
     * @param phone 手机号
     */
    @GetMapping("/send")
    public R<Void> send(@NotBlank(message = "手机号不能为空") String phone) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + phone;
        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(SMS_CODE_TTL_MINUTES));
        // 验证码模板id 自行处理（查数据库或写死均可）
        String templateId = "";
        LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
        map.put("code", code);
        SmsBlend smsBlend = SmsFactory.getSmsBlend("config1");
        SmsResponse smsResponse = smsBlend.sendMessage(phone, templateId, map);
        if (!smsResponse.isSuccess()) {
            log.error("验证码短信发送异常 => {}", smsResponse);
            return R.fail(smsResponse.getData().toString());
        }
        return R.ok();
    }

    /**
     * 校验短信验证码
     *
     * @param request 校验请求（phone + code）
     */
    @PostMapping("/verify")
    public R<Void> verify(@Validated @RequestBody SmsVerifyRequest request) {
        String key = GlobalConstants.CAPTCHA_CODE_KEY + request.getPhone();
        String storedCode = RedisUtils.getCacheObject(key);
        if (storedCode == null) {
            return R.fail("验证码已过期或不存在");
        }
        if (!storedCode.equals(request.getCode())) {
            return R.fail("验证码错误");
        }
        // 验证通过后删除验证码，防止重复使用
        RedisUtils.deleteObject(key);
        return R.ok();
    }

    @Data
    public static class SmsVerifyRequest {
        @NotBlank(message = "手机号不能为空")
        private String phone;
        @NotBlank(message = "验证码不能为空")
        private String code;
    }

}
