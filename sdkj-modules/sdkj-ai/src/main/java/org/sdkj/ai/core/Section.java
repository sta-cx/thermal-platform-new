package org.sdkj.ai.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Section {

    private String id;

    private SectionType type;

    private String title;

    private Object content;

    private List<Map<String, String>> actions;
}
