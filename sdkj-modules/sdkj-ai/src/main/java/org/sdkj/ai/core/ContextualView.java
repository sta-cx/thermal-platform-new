package org.sdkj.ai.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContextualView {

    private String viewId;

    private List<Section> sections = new ArrayList<>();
}
