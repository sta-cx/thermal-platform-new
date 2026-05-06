package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtMeterVendorVo;

/**
 * 仪表厂商
 * 迁移自旧系统 MtMeterVendor
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_vendor")
@AutoMapper(target = MtMeterVendorVo.class)
public class MtMeterVendor extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    /** 厂商编码 */
    private String code;

    /** 厂商名称 */
    private String name;

    /** 厂商联系人 */
    private String contacts;

    /** 联系人电话 */
    private String tele;

    /** 地址 */
    private String address;

    /** 排序 */
    private String seq;

    /** 是否启用 (0=禁用 1=启用) */
    private Integer isEnabled;

}
