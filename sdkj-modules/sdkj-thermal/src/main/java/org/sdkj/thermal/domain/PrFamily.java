package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrFamilyVo;

/**
 * 家庭成员信息
 * 迁移自旧系统 PrFamily
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_family")
@AutoMapper(target = PrFamilyVo.class)
public class PrFamily extends BaseEntity {

    @TableId(value = "id")
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
