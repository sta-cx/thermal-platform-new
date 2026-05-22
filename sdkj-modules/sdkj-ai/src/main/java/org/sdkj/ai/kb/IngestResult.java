package org.sdkj.ai.kb;

public record IngestResult(Long docId, int chunkCount, boolean duplicate) {

    public static IngestResult ingested(Long docId, int chunkCount) {
        return new IngestResult(docId, chunkCount, false);
    }

    public static IngestResult duplicate(Long existingDocId) {
        return new IngestResult(existingDocId, 0, true);
    }
}
