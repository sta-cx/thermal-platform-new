package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分组数据对象
 */
@Data
public class GroupData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通道号 */
    private String chanNum;

    /** 平均回水温度 */
    private Integer outTempPJ;

    /** 调节步长 */
    private Integer stride;

    /** 回水温度偏差值 */
    private Integer outTempDeviation;

}
