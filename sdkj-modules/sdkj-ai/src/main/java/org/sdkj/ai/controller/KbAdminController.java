package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.kb.QdrantCollectionManager;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/kb/admin")
@DS("master")
@RequiredArgsConstructor
public class KbAdminController {

    private final QdrantCollectionManager collectionManager;

    @PostMapping("/reset-collection")
    @SaCheckPermission("ai:kb:admin")
    public R<Void> resetCollection() {
        String tenantId = LoginHelper.getTenantId();
        collectionManager.deleteCollection(tenantId);
        collectionManager.ensureCollection(tenantId);
        return R.ok();
    }
}
