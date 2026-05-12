package org.sdkj.system.ai.prompts;

import jakarta.annotation.Resource;
import org.sdkj.ai.core.ContextualPrompt;
import org.sdkj.ai.core.ContextualRequest;
import org.sdkj.ai.core.ContextualView;
import org.sdkj.ai.core.PromptPayload;
import org.sdkj.system.ai.views.UserListView;
import org.sdkj.system.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserListPrompt implements ContextualPrompt {

    @Resource
    private SysUserMapper userMapper;

    @Override
    public String[] routePatterns() {
        return new String[] { "/system/user" };
    }

    @Override
    public String displayName() {
        return "用户列表";
    }

    @Override
    public PromptPayload buildPrompt(ContextualRequest ctx) {
        long total = userMapper.selectCount(null);

        Map<String, Object> vars = new HashMap<>();
        vars.put("total", total);
        vars.put("route", ctx.getRoute());
        vars.put("filters", ctx.getFilters());

        String systemPrompt = """
            你正在为 SDKJ 智慧供热平台的"用户列表"页面生成 AI 旁注。
            基于下方数据快照,产出 2-3 个 section,严格遵循 JSON Schema:
              sections[].type 必须是 STAT / WARNING / SUGGESTION / NARRATIVE / LINK 之一
              STAT 的 content 是 [{label,value}, ...]
              NARRATIVE 的 content 是字符串
              SUGGESTION 的 content 是 [{text,actionUrl?}, ...]
            不要输出任何额外字段。不要编造没有的数据。
            """;

        String userPrompt = """
            页面:{route}
            用户总数:{total}
            筛选条件:{filters}

            请生成旁注。
            """;

        return PromptPayload.builder()
            .systemPrompt(systemPrompt)
            .userPromptTemplate(userPrompt)
            .templateVars(vars)
            .cacheKey("user-list-snapshot")
            .build();
    }

    @Override
    public Class<? extends ContextualView> viewSchema() {
        return UserListView.class;
    }
}
