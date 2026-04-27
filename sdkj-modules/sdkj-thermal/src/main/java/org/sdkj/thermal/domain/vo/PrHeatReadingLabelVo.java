package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 远传抄表标签 View Object
 * 用于 pageList 接口的请求体，支持按标签/房间号筛选
 */
@Data
public class PrHeatReadingLabelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    private String id;

    /** 标签值（房间号/表号等） */
    private String label;

    /** 热表档案编号 */
    private String meterArcCode;

    /** 物业公司名称 */
    private String companyName;

    /** 小区名称 */
    private String orgName;

    /** 楼栋名称 */
    private String buildingName;

    /** 单元编码 */
    private String unitCode;

    /** 房间号 */
    private String roomNum;

    /** 标签日期 */
    private String labelDate;
}
