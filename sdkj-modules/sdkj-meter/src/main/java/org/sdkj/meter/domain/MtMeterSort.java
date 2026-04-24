package org.sdkj.meter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.sdkj.common.mybatis.core.domain.BaseEntity;
import org.sdkj.meter.domain.vo.MtMeterSortVo;

/**
 * 仪表分类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mt_meter_sort")
@AutoMapper(target = MtMeterSortVo.class)
public class MtMeterSort extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 编号 */
    private String code;

    /** 名称 */
    private String name;

    /** 型号 */
    private String model;

    /** 厂商ID */
    private String vendorId;

    /** 是否一卡通 */
    private Integer isOnecard;

    /** 计费模式 0按量 1按金额 2按时间 */
    private String measureType;

    /** 排序 */
    private String seq;

    /** 仪表类型 01=电表 02=水表 03=热力表 04=燃气表 */
    private String meterType;
}
