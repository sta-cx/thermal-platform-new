package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtGasArchive;

/**
 * 燃气表档案视图对象
 * 迁移自旧系统 MtGasArchive
 */
@Data
@AutoMapper(target = MtGasArchive.class)
public class MtGasArchiveVo {

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

    /** 创建时间 */
    private java.util.Date createTime;

}
