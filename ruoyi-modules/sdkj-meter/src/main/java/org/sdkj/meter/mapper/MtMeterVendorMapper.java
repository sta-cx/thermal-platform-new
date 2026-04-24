package org.sdkj.meter.mapper;

import org.apache.ibatis.annotations.Param;
import org.sdkj.common.mybatis.core.mapper.BaseMapperPlus;
import org.sdkj.meter.domain.MtMeterVendor;
import org.sdkj.meter.domain.vo.MtMeterVendorVo;

import java.util.List;

/**
 * 仪表厂商 Mapper
 * 迁移自旧系统 MtMeterVendorMapper
 */
public interface MtMeterVendorMapper extends BaseMapperPlus<MtMeterVendor, MtMeterVendorVo> {

    /**
     * 校验厂商名称是否重复
     * @param name 厂商名称
     * @param id 排除的ID（修改时）
     * @return 重复数量
     */
    int verifyName(@Param("name") String name, @Param("id") String id);

    /**
     * 统计厂商被仪表分类引用的次数
     * @param vendorId 厂商ID
     * @return 引用次数
     */
    int countByVendorId(@Param("vendorId") String vendorId);

    /**
     * 查询所有启用的厂商
     * @return 启用的厂商列表
     */
    List<MtMeterVendorVo> selectAllEnabled();

}
