package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtTcValveVo;

/**
 * 阀门档案
 * 迁移自旧系统 MtTcValve
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_tc_valve")
@AutoMapper(target = MtTcValveVo.class)
public class MtTcValve extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
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

    /** 是否动作 (0=否 1=是) */
    private Integer isAction;

    /** 安装位置 */
    private String installSite;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
