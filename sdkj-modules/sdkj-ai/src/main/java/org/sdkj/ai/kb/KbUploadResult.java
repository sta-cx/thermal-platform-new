package org.sdkj.ai.kb;

public record KbUploadResult(
    String fileName,
    Status status,
    Long docId,
    Integer chunkCount,
    String errorMessage
) {
    public enum Status { SUCCESS, DUPLICATE, FAILED }

    public static KbUploadResult success(String name, Long docId, int chunks) {
        return new KbUploadResult(name, Status.SUCCESS, docId, chunks, null);
    }
    public static KbUploadResult duplicate(String name, Long existingDocId) {
        return new KbUploadResult(name, Status.DUPLICATE, existingDocId, null, "已存在");
    }
    public static KbUploadResult failed(String name, String error) {
        return new KbUploadResult(name, Status.FAILED, null, null, error);
    }
}
