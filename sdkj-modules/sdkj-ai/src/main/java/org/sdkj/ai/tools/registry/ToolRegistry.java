package org.sdkj.ai.tools.registry;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.sdkj.ai.config.AiProperties;
import org.sdkj.ai.tools.annotation.RiskLevel;
import org.sdkj.ai.tools.annotation.WriteTool;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 启动时扫描所有 @Tool 方法,解析 @WriteTool 元数据,管理黑名单(application-thermal.yml: ai.tools.disabled)。
 * 暴露 byName(name) 给 ConfirmationGateAdvisor 用于 Tool 拦截决策。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolRegistry {

    private final ConfigurableListableBeanFactory beanFactory;
    private final AiProperties aiProperties;

    /** fullName → metadata */
    private final Map<String, ToolMetadata> byFullName = new ConcurrentHashMap<>();
    /** Spring AI 生成的短名(methodName) → metadata，用于查找 LLM 返回的 tool call */
    private final Map<String, ToolMetadata> byMethodName = new ConcurrentHashMap<>();

    @PostConstruct
    public void scan() {
        Set<String> disabled = new HashSet<>(
            aiProperties.getTools() == null ? List.of() : aiProperties.getTools().getDisabled()
        );

        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Class<?> type;
            try { type = beanFactory.getType(beanName); } catch (Exception e) { continue; }
            if (type == null) continue;
            if (disabled.contains(beanName)) {
                log.warn("[ToolRegistry] Bean {} disabled by config (ai.tools.disabled)", beanName);
                continue;
            }

            for (Method m : type.getMethods()) {
                Tool t = m.getAnnotation(Tool.class);
                if (t == null) continue;

                WriteTool w = m.getAnnotation(WriteTool.class);
                ToolMetadata md = new ToolMetadata(
                    beanName,
                    m.getName(),
                    beanName + "." + m.getName(),
                    t.description(),
                    w == null ? RiskLevel.LOW : w.risk(),
                    w != null && w.confirm(),
                    w == null ? "" : w.permission(),
                    m,
                    beanFactory.getBean(beanName)
                );
                byFullName.put(md.fullName(), md);
                if (byMethodName.putIfAbsent(m.getName(), md) != null) {
                    log.warn("[ToolRegistry] duplicate method name '{}' across beans, lookup may be ambiguous", m.getName());
                }
                log.info("[ToolRegistry] registered tool: {} risk={} confirm={}",
                    md.fullName(), md.risk(), md.requireConfirm());
            }
        }
    }

    /**
     * 按 fullName(beanName.methodName) 查找。用于 confirm 端点恢复 PendingToolCall。
     */
    public ToolMetadata byName(String fullName) {
        return byFullName.get(fullName);
    }

    /**
     * 按方法名查找。Spring AI 生成 tool name 时只使用方法名(不含 beanName 前缀)，
     * 因此 LLM 返回的 tool call name 需要靠此方法匹配。
     */
    public ToolMetadata resolve(String toolCallName) {
        ToolMetadata md = byFullName.get(toolCallName);
        return md != null ? md : byMethodName.get(toolCallName);
    }

    public Collection<ToolMetadata> all() {
        return Collections.unmodifiableCollection(byFullName.values());
    }

    /**
     * 返回所有去重的 Tool Bean 实例(用于 ChatClient.prompt().tools() 注册)。
     * 同一个 Bean 可能有多个 @Tool 方法,但只需注册一次。
     */
    public Object[] getToolBeans() {
        return byFullName.values().stream()
            .map(ToolMetadata::bean)
            .distinct()
            .toArray(Object[]::new);
    }
}
