package org.sdkj.ai.core;

/**
 * @deprecated 由 GenericPromptBuilder + LLM 替代，不再需要硬编码 fallback 视图。
 */
@Deprecated(since = "2026-05-13", forRemoval = true)
public class GenericContextualView extends ContextualView {

    public static ContextualView fallbackFor(ContextualRequest req) {
        ContextualView v = new ContextualView();
        v.setViewId("fallback-" + System.currentTimeMillis());

        Section.Narrative narrative = new Section.Narrative();
        narrative.setId("fallback-1");
        narrative.setType(SectionType.NARRATIVE);
        narrative.setTitle("AI 旁注");
        narrative.setContent(
            "当前页面(" + (req.getRouteName() != null ? req.getRouteName() : req.getRoute())
            + ")暂未接入 AI 旁注。后续版本会扩展支持。"
        );
        v.getSections().add(narrative);
        return v;
    }
}
