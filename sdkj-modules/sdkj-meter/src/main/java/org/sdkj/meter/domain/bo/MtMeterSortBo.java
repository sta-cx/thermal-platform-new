package org.sdkj.meter.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.MtMeterSort;

/**
 * 仪表分类业务对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MtMeterSort.class, reverseConvertGenerate = false)
public class MtMeterSortBo extends BaseEntity {

    /** 主键 */
    private Long id;

    /** 编号 */
    @NotBlank(message = "编号不能为空")
    private String code;

    /** 名称 */
    @NotBlank(message = "名称不能为空")
    private String name;

    /** 型号 */
    private String model;

    /** 厂商ID */
    private Long vendorId;

    /** 是否一卡通 */
    private Integer isOnecard;

    /** 计费模式 0按量 1按金额 2按时间 */
    private String measureType;

    /** 排序 */
    private String seq;

    /** 仪表类型 01=电表 02=水表 03=热力表 04=燃气表 */
    private String meterType;

}
