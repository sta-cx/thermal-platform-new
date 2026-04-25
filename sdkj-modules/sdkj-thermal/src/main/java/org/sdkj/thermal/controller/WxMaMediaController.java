package org.sdkj.thermal.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/thermal/wx/media/{appid}")
public class WxMaMediaController {

    @SaIgnore
    @PostMapping("/upload")
    public Object uploadMedia(@PathVariable String appid) {
        // TODO: Phase 5d - 上传临时素材到微信服务器
        return java.util.Collections.emptyList();
    }

    @SaIgnore
    @GetMapping("/download/{mediaId}")
    public Object getMedia(@PathVariable String appid, @PathVariable String mediaId) {
        // TODO: Phase 5d - 下载微信临时素材
        return null;
    }
}
