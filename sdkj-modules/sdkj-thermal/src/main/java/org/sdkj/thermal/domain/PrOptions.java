package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrOptionsVo;

/**
 * 系统选项
 * 迁移自旧系统 PrOptions
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_options")
@AutoMapper(target = PrOptionsVo.class)
public class PrOptions extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 公司ID */
    private String companyId;

    /** 组织/小区ID */
    private String orgId;

    /** 级别 */
    private String level;

    /** 是否禁止购电 */
    private Boolean forbiddenBuyElectric;

    /** 是否禁止购水 */
    private Boolean forbiddenBuyWater;
}
