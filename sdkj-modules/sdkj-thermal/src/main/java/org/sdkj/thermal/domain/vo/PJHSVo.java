package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 平均回水温度视图对象
 */
@Data
public class PJHSVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通道号 */
    private String chanNum;

    /** 平均回水温度 */
    private Double outTempPJ;

}
