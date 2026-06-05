package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;

/**
 * 热力表档案
 * 迁移自旧系统 MtHeatArchive
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_heat_archive")
@AutoMapper(target = MtHeatArchiveVo.class)
public class MtHeatArchive extends BaseEntity {

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

    /** 备注 */
    private String remark;

}
