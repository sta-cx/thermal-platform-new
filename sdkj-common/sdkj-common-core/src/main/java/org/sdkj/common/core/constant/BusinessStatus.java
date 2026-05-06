package org.sdkj.common.core.constant;

/**
 * 业务状态常量 — 替代硬编码的 "0"/"1" 魔法值
 */
public interface BusinessStatus {

    String DEL_FLAG_NORMAL = "0";
    String DEL_FLAG_DELETED = "1";

    String IS_CALC_NO = "0";
    String IS_CALC_YES = "1";

    String IS_CHARGED_NO = "0";
    String IS_CHARGED_YES = "1";

    String IS_ENABLED_YES = "1";
    String IS_ENABLED_NO = "0";

    String ROOT_PARENT_ID = "0";

    String VISIBLE_YES = "1";
    String VISIBLE_NO = "0";
}
