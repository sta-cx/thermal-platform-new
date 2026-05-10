package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.HtScope;

/**
 * 控制范围视图对象
 */
@Data
@AutoMapper(target = HtScope.class)
public class HtScopeVo {

    private Long id;
    private Long tasksId;
    private String orgId;
    private Long buildingId;
    private Long unitId;
    private Long houseId;
    private String meterNum;
    private Long meterId;
    private String meterArcCode;
    private String concentratorCode;
    private String imei;
    private String deviceId;
    private Integer status;
    private Integer isSpecial;
    private String dtuNum;
    private String chanNum;
}
