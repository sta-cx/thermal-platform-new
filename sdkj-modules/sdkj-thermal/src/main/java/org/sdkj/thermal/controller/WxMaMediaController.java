package org.sdkj.thermal.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/media/{appid}")
public class WxMaMediaController {

    private final WxMaService wxMaService;

    @SaIgnore
    @PostMapping("/upload")
    public R<List<String>> uploadMedia(@PathVariable String appid,
                                        @RequestParam("files") List<MultipartFile> files) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("未找到对应appid=[" + appid + "]的配置");
        }
        List<String> result = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                File tempFile = File.createTempFile("wx_media_", ".tmp");
                file.transferTo(tempFile);
                WxMediaUploadResult uploadResult = wxMaService.getMediaService()
                        .uploadMedia(WxMaConstants.KefuMsgType.IMAGE, tempFile);
                result.add(uploadResult.getMediaId());
                tempFile.delete();
            }
            log.info("上传小程序素材成功: appid={}, count={}", appid, result.size());
            return R.ok(result);
        } catch (WxErrorException | IOException e) {
            log.error("上传素材失败: appid={}", appid, e);
            return R.fail("上传素材失败: " + e.getMessage());
        } finally {
            WxMaConfigHolder.remove();
        }
    }

    @SaIgnore
    @GetMapping("/download/{mediaId}")
    public R<Map<String, Object>> getMedia(@PathVariable String appid, @PathVariable String mediaId) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("未找到对应appid=[" + appid + "]的配置");
        }
        try {
            File media = wxMaService.getMediaService().getMedia(mediaId);
            Map<String, Object> result = Map.of("mediaId", mediaId, "filePath", media.getAbsolutePath());
            return R.ok(result);
        } catch (WxErrorException e) {
            log.error("下载素材失败: appid={}, mediaId={}", appid, mediaId, e);
            return R.fail("下载素材失败: " + e.getMessage());
        } finally {
            WxMaConfigHolder.remove();
        }
    }
}
