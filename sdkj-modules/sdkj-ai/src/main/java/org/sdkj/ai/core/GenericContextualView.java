package org.sdkj.ai.core;

public class GenericContextualView extends ContextualView {

    public static ContextualView fallbackFor(ContextualRequest req) {
        ContextualView v = new ContextualView();
        v.setViewId("fallback-" + System.currentTimeMillis());

        Section narrative = new Section();
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
