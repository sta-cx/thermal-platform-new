package org.dromara.meter.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.meter.domain.MtMeterSort;

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
}
