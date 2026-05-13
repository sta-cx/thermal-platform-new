package org.sdkj.ai.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

/**
 * 旁注面板中的一个 section。LLM 必须按 SectionType 选择对应子类型,
 * content 形态由子类强类型约束,避免 LLM 自由乱填导致前端渲染失败。
 *
 * Jackson 多态(@JsonTypeInfo + @JsonSubTypes)既支持序列化输出,
 * 又让 Spring AI 的 BeanOutputConverter 生成 JSON Schema 时把每种 type 的
 * content 结构都体现给 LLM。
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Section.Stat.class,       name = "STAT"),
    @JsonSubTypes.Type(value = Section.Warning.class,    name = "WARNING"),
    @JsonSubTypes.Type(value = Section.Suggestion.class, name = "SUGGESTION"),
    @JsonSubTypes.Type(value = Section.Narrative.class,  name = "NARRATIVE"),
    @JsonSubTypes.Type(value = Section.Link.class,       name = "LINK"),
})
public abstract class Section {

    /** v-for key,LLM 生成或后端补 */
    private String id;

    /** section 类型,必须与子类绑定的 name 一致(由 Jackson 多态机制保证) */
    private SectionType type;

    /** 标题,如"总览"/"异常"/"建议" */
    private String title;

    /** 可选操作按钮(复制/标记/跳转),非核心展示数据 */
    private List<SectionAction> actions;

    /** 📊 数据统计 */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class Stat extends Section {
        /** 统计条目,顺序展示 */
        private List<StatItem> content;
    }

    /** ⚠️ 异常/风险 */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class Warning extends Section {
        private List<WarningItem> content;
    }

    /** 💡 建议/推荐 */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class Suggestion extends Section {
        private List<SuggestionItem> content;
    }

    /** 📖 自然语言段落 */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class Narrative extends Section {
        /** Markdown / 纯文本 */
        private String content;
    }

    /** 🔗 跳转链接 */
    @Data
    @lombok.EqualsAndHashCode(callSuper = true)
    public static class Link extends Section {
        private List<LinkItem> content;
    }

    // ───── 操作按钮 ─────

    @Data
    public static class SectionAction {
        private String label;
        private String type;
        private String url;
    }

    // ───── 内容条目 ─────

    @Data
    public static class StatItem {
        /** 标签,如"用户总数" */
        private String label;
        /** 值,number 或 string 均可,直接由 LLM 决定 */
        private Object value;
    }

    @Data
    public static class WarningItem {
        /** "high" / "medium" / "low" */
        private String level;
        /** 文案 */
        private String text;
        /** 可选关联实体 ID,前端可点击跳详情 */
        private String entityId;
    }

    @Data
    public static class SuggestionItem {
        /** 建议文案 */
        private String text;
        /** 可选跳转 URL,前端用 router.push */
        private String actionUrl;
    }

    @Data
    public static class LinkItem {
        /** 链接文案 */
        private String text;
        /** 内部路由或外部 URL */
        private String url;
    }
}
