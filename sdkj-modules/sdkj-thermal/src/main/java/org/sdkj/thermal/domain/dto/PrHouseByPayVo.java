package org.sdkj.thermal.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 房屋缴费信息 VO，用于批量阀门操作的请求参数
 */
@Data
public class PrHouseByPayVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 仪表编号 */
    private String meterNum;

    /** 仪表档案编码 */
    private String meterArcCode;

    /** 小区ID */
    private String orgId;

    /** 公司ID */
    private String companyId;

    /** 房屋ID */
    private Long houseId;

    /** 楼宇名称 */
    private String buildingName;

    /** 房间号 */
    private String roomNum;

    /** 小区名称 */
    private String orgName;

    /** 单元编码 */
    private String unitCode;

    /** 缴费状态 */
    private String payStatus;

    /** 阀门状态 */
    private String valveStatus;
}
