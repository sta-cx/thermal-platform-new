package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.thermal.domain.vo.PrPrintTemplateVo;

/**
 * 打印模板配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pr_print_template")
@AutoMapper(target = PrPrintTemplateVo.class)
public class PrPrintTemplate extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 模板名称 */
    private String name;

    /** 模板内容 */
    private String templateContent;

    /** 序列号 */
    private String serialNum;

    /** 公司ID */
    private String companyId;

    /** 小区ID */
    private String orgId;
}
