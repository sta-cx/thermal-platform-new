package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtMeterVendor;

/**
 * 仪表厂商业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtMeterVendor.class, reverseConvertGenerate = false)
public class MtMeterVendorBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 厂商编码 */
    @NotBlank(message = "厂商编码不能为空")
    private String code;

    /** 厂商名称 */
    @NotBlank(message = "厂商名称不能为空")
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
