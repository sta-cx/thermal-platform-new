package org.sdkj.ai.kb;

import java.util.List;

/**
 * RAG retrieval result carrying both prompt fragments and citation metadata.
 *
 * @param fragments text fragments to prepend into the LLM prompt
 * @param citations structured citation metadata for the frontend
 */
public record RetrievalResult(List<String> fragments, List<Citation> citations) {

    public boolean isEmpty() {
        return fragments == null || fragments.isEmpty();
    }
}
