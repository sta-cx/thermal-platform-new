package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtMeterVendor;

/**
 * 仪表厂商视图对象
 * 迁移自旧系统 MtMeterVendor
 */
@Data
@AutoMapper(target = MtMeterVendor.class)
public class MtMeterVendorVo {

    private String id;

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
