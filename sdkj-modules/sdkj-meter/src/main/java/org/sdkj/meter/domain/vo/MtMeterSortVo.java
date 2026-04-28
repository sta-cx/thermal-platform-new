package org.sdkj.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.sdkj.meter.domain.MtMeterSort;

/**
 * 仪表分类视图对象
 */
@Data
@AutoMapper(target = MtMeterSort.class)
public class MtMeterSortVo {

    private String id;
    private String code;
    private String name;
    private String model;
    private String vendorId;
    private Integer isOnecard;
    private String measureType;
    private String seq;
    private String meterType;

    /** 创建时间 */
    private java.util.Date createTime;
}
