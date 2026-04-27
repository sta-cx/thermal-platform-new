package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.AgPropertyVo;

/**
 * 代理商关联物业表
 * 对应 ag_company_property 表，记录代理商与物业公司的绑定关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ag_company_property")
@AutoMapper(target = AgPropertyVo.class)
public class AgProperty extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 代理商公司ID */
    private String agentCompanyId;

    /** 物业公司ID */
    private String propertyCompanyId;

    /** 是否审核 0未审核 1已审核 */
    private Integer isAudited;

    /** 是否启用 0未启用 1启用 */
    private Integer isEnabled;

    /** 物业公司名称（查询用） */
    @TableField(exist = false)
    private String propertyName;

    /** 物业公司编码（查询用） */
    @TableField(exist = false)
    private String propertyCode;
}
