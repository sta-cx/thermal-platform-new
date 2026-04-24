package org.dromara.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.thermal.domain.PrHouse;

import java.io.Serial;
import java.io.Serializable;

/**
 * 房屋信息 View Object
 */
@Data
@AutoMapper(target = PrHouse.class)
public class PrHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String roomNum;
    private String buildingId;
    private String unitCode;
    private Integer floor;
    private String orgId;
    private String companyId;
    private String itemGroup;
    private String itemCode;
    private String itemName;
    private String userName;
    private String phone;
    private String userId;
}
