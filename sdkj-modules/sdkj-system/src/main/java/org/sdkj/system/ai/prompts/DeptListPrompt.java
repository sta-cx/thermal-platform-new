package org.sdkj.system.ai.prompts;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualRequest;
import org.sdkj.ai.core.ContextualView;
import org.sdkj.ai.core.PromptPayload;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.system.ai.views.DeptListView;
import org.sdkj.system.domain.SysDept;
import org.sdkj.system.mapper.SysDeptMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeptListPrompt implements ContextualPrompt {

    @Resource
    private SysDeptMapper deptMapper;

    @Override
    public String[] routePatterns() {
        return new String[] { "/system/dept" };
    }

    @Override
    public String displayName() {
        return "部门列表";
    }

    @Override
    public PromptPayload buildPrompt(ContextualRequest ctx) {
        String tenantId = LoginHelper.getTenantId();
        long total = deptMapper.selectCount(
            new LambdaQueryWrapper<SysDept>().eq(SysDept::getTenantId, tenantId)
        );

        Map<String, Object> vars = new HashMap<>();
        vars.put("total", total);
        vars.put("route", ctx.getRoute());

        String systemPrompt = """
            你正在为 SDKJ 智慧供热平台的"部门列表"页面生成 AI 旁注。
            产出 2-3 个 section,严格遵循 JSON Schema。
            """;

        String userPrompt = """
            页面:{route}
            部门总数:{total}

            请生成旁注。
            """;

        return PromptPayload.builder()
            .systemPrompt(systemPrompt)
            .userPromptTemplate(userPrompt)
            .templateVars(vars)
            .cacheKey("dept-list-snapshot")
            .build();
    }

    @Override
    public Class<? extends ContextualView> viewSchema() {
        return DeptListView.class;
    }
}
