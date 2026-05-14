package org.sdkj.ai.kb;

import lombok.Getter;

/**
 * KB 摄取流水线异常 — 让 controller 把 500 返给前端,而非误返 200+docId。
 * 携带 docId 让前端可定位"哪个文档已经在 FAILED 状态",可选择"重试"或"删除"。
 */
@Getter
public class KbIngestException extends RuntimeException {

    private final Long docId;

    public KbIngestException(String message, Long docId, Throwable cause) {
        super(message, cause);
        this.docId = docId;
    }
}
