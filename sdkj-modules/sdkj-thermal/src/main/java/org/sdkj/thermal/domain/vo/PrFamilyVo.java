package org.sdkj.thermal.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.thermal.domain.PrFamily;

/**
 * 家庭成员信息视图对象
 */
@Data
@AutoMapper(target = PrFamily.class)
public class PrFamilyVo {

    private Long id;

    /** 客户证件号 */
    private String userIdNo;

    /** 家庭成员姓名 */
    private String name;

    /** 性别 */
    private Integer sex;

    /** 联系地址 */
    private String contactAddr;

    /** 工作单位 */
    private String employer;

    /** 家庭成员证件号 */
    private String familyIdNo;

    /** 与户主关系 */
    private String relationType;

    /** 房屋ID */
    private Long houseId;

}
