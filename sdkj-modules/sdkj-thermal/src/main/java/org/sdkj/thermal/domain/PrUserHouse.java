package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrUserHouseVo;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_user_house")
@AutoMapper(target = PrUserHouseVo.class)
public class PrUserHouse extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String userId;

    private String userName;

    private String phone;

    private Long houseId;

    /** 1=业主, 2=租户, 3=家属 */
    private String relationType;

    private String remark;

    private String orgId;

    /** 1=PC, 2=APP, 3=系统迁移 */
    private String recordSource;

    @TableLogic(value = "0", delval = "1")
    private String delFlag;
}
