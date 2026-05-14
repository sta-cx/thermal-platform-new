package org.sdkj.ai.kb;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChunkResult {
    private int index;
    private String text;
    private int estimatedTokens;
}
