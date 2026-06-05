package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtTcArchive;

/**
 * 温控器档案视图对象
 * 迁移自旧系统 MtTcArchive
 */
@Data
@AutoMapper(target = MtTcArchive.class)
public class MtTcArchiveVo {

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

    /** 类型 */
    private String type;

    /** 是否动作 (0=否 1=是) */
    private Integer isAction;

    /** 安装位置 */
    private String installSite;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

    /** 创建时间 */
    private java.util.Date createTime;

    /** 备注 */
    private String remark;

}
