package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/thermal/wx/media/{appid}")
public class WxMaMediaController {

    private static final Map<String, byte[]> MEDIA_STORE = new ConcurrentHashMap<>();

    @SaIgnore
    @PostMapping("/upload")
    public R<Object> uploadMedia(@PathVariable String appid) {
        log.info("上传临时素材: appid={}", appid);
        // Phase 6: WxMaService.getMediaService().uploadMedia("image", file)
        // Phase 6: 返回 mediaId 和 url
        return R.fail("素材上传 Phase 6 实现 — 需 WxMaService SDK");
    }

    @SaIgnore
    @GetMapping("/download/{mediaId}")
    public Object getMedia(@PathVariable String appid, @PathVariable String mediaId) {
        if (mediaId == null || mediaId.isEmpty()) return R.fail("mediaId 不能为空");
        log.info("下载临时素材: appid={}, mediaId={}", appid, mediaId);
        // Phase 6: WxMaService.getMediaService().getMedia(mediaId) → InputStream
        byte[] data = MEDIA_STORE.get(mediaId);
        if (data == null) return R.fail("素材不存在或已过期");
        return data;
    }
}
