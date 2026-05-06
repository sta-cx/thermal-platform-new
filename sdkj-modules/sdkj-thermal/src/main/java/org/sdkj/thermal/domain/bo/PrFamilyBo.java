package org.sdkj.thermal.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.PrFamily;

/**
 * 家庭成员信息业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PrFamily.class, reverseConvertGenerate = false)
public class PrFamilyBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 客户证件号 */
    @NotBlank(message = "客户证件号不能为空")
    private String userIdNo;

    /** 家庭成员姓名 */
    @NotBlank(message = "家庭成员姓名不能为空")
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
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;

}
