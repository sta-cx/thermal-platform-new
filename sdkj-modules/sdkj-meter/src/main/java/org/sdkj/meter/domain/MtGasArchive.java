package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtGasArchiveVo;

/**
 * 燃气表档案
 * 迁移自旧系统 MtGasArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_gas_archive")
@AutoMapper(target = MtGasArchiveVo.class)
public class MtGasArchive extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 分类ID */
    private Long sortId;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
