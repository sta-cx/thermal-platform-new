package org.sdkj.ai.tools.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.sdkj.ai.tools.registry.ToolMetadata;
import org.sdkj.ai.tools.registry.ToolRegistry;
import org.sdkj.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * 管理员视角:已注册的 @Tool Bean 看板。
 * 用于运维核对 ToolRegistry 是否漏扫,以及辅助配置 ai.tools.disabled 黑名单。
 */
@RestController
@RequestMapping("/ai/admin/tools")
@RequiredArgsConstructor
public class AiToolsAdminController {

    private final ToolRegistry registry;

    public record ToolView(
        String beanName,
        String methodName,
        String fullName,
        String description,
        String risk,
        boolean requireConfirm,
        String permission
    ) {}

    @SaCheckPermission("ai:admin:tools:view")
    @GetMapping
    public R<List<ToolView>> list() {
        List<ToolView> views = registry.all().stream()
            .map(md -> new ToolView(
                md.beanName(), md.methodName(), md.fullName(),
                md.description(), md.risk().name(),
                md.requireConfirm(), md.permission()
            ))
            .sorted(Comparator
                .comparing(ToolView::risk)
                .thenComparing(ToolView::fullName))
            .toList();
        return R.ok(views);
    }
}
