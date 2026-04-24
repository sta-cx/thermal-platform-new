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

    private String id;
    private String tasksId;
    private String orgId;
    private String buildingId;
    private String unitId;
    private String companyId;
    private String houseId;
    private String meterNum;
    private String meterId;
    private String meterArcCode;
    private String concentratorCode;
    private String imei;
    private String deviceId;
    private Integer status;
    private Integer isSpecial;
    private String dtuNum;
    private String chanNum;
}
