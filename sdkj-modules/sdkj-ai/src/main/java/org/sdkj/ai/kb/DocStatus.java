package org.sdkj.ai.kb;

/**
 * 知识库文档处理状态
 */
public enum DocStatus {
    UPLOADED,    // 上传完成,等切片
    CHUNKED,     // 切片完成,等 embedding
    EMBEDDED,    // 已入 Qdrant,可被 RAG 命中
    FAILED       // 失败,error_message 记原因
}
