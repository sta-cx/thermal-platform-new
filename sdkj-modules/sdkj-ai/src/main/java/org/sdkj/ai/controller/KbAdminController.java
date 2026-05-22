package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.kb.QdrantCollectionManager;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/kb/admin")
@RequiredArgsConstructor
public class KbAdminController {

    private final QdrantCollectionManager collectionManager;

    @PostMapping("/reset-collection")
    @SaCheckPermission("ai:kb:admin")
    public R<Void> resetCollection(@RequestParam String tenantId) {
        collectionManager.deleteCollection(tenantId);
        collectionManager.ensureCollection(tenantId);
        return R.ok();
    }
}
