package org.dromara.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.meter.domain.MtHeatArchive;

/**
 * 热力表档案视图对象
 * 迁移自旧系统 MtHeatArchive
 */
@Data
@AutoMapper(target = MtHeatArchive.class)
public class MtHeatArchiveVo {

    private String id;

    /** 分类ID */
    private String sortId;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 规格 */
    private String specification;

    /** 型号 */
    private String model;

    /** 类型 */
    private String type;

    /** 是否动作 */
    private Integer isAction;

    /** 安装位置 */
    private String installSite;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
