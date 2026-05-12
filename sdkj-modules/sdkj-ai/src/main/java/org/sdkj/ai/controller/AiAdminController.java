package org.sdkj.ai.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualPromptRegistry;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/admin")
@RequiredArgsConstructor
public class AiAdminController {

    private final ContextualPromptRegistry registry;

    @SaCheckRole("admin")
    @GetMapping("/registry")
    public R<List<Map<String, Object>>> listRegistry() {
        List<Map<String, Object>> result = registry.listAll().stream()
            .map(p -> Map.<String, Object>of(
                "name", p.getClass().getSimpleName(),
                "displayName", p.displayName(),
                "routePatterns", List.of(p.routePatterns()),
                "requiredPermission", p.requiredPermission() == null ? "-" : p.requiredPermission(),
                "cacheTtl", p.cacheTtl().toString()
            ))
            .toList();
        return R.ok(result);
    }
}
