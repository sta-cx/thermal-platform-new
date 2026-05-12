package org.sdkj.system.ai.prompts;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualRequest;
import org.sdkj.ai.core.ContextualView;
import org.sdkj.ai.core.PromptPayload;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.system.ai.views.RoleListView;
import org.sdkj.system.domain.SysRole;
import org.sdkj.system.mapper.SysRoleMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RoleListPrompt implements ContextualPrompt {

    @Resource
    private SysRoleMapper roleMapper;

    @Override
    public String[] routePatterns() {
        return new String[] { "/system/role" };
    }

    @Override
    public String displayName() {
        return "角色列表";
    }

    @Override
    public PromptPayload buildPrompt(ContextualRequest ctx) {
        String tenantId = LoginHelper.getTenantId();
        long total = roleMapper.selectCount(
            new LambdaQueryWrapper<SysRole>().eq(SysRole::getTenantId, tenantId)
        );

        Map<String, Object> vars = new HashMap<>();
        vars.put("total", total);
        vars.put("route", ctx.getRoute());

        String systemPrompt = """
            你正在为 SDKJ 智慧供热平台的"角色列表"页面生成 AI 旁注。
            产出 2-3 个 section,严格遵循 JSON Schema(STAT/WARNING/SUGGESTION/NARRATIVE/LINK)。
            不要编造没有的数据。
            """;

        String userPrompt = """
            页面:{route}
            角色总数:{total}

            请生成旁注。
            """;

        return PromptPayload.builder()
            .systemPrompt(systemPrompt)
            .userPromptTemplate(userPrompt)
            .templateVars(vars)
            .cacheKey("role-list-snapshot")
            .build();
    }

    @Override
    public Class<? extends ContextualView> viewSchema() {
        return RoleListView.class;
    }
}
