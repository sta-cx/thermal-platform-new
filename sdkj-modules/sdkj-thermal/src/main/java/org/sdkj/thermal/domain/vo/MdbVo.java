package org.sdkj.thermal.domain.vo;

import lombok.Data;

/**
 * MDB 授权码导入 VO
 */
@Data
public class MdbVo {
    private String pro;
    private String code;
    private String name;
    private String address;
    private String heatPayCode;
    private String houseId;
}
