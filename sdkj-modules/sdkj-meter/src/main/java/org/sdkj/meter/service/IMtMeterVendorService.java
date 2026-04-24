package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtMeterVendor;
import org.sdkj.meter.domain.vo.MtMeterVendorVo;

import java.util.List;

/**
 * 仪表厂商 Service 接口
 * 迁移自旧系统 MtMeterVendorService
 */
public interface IMtMeterVendorService extends IService<MtMeterVendor> {

    /**
     * 分页查询仪表厂商列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtMeterVendorVo> selectPageList(LambdaQueryWrapper<MtMeterVendor> lqw, PageQuery pageQuery);

    /**
     * 校验厂商名称是否重复
     * @param name 厂商名称
     * @param id 排除的ID（修改时）
     * @return 重复数量
     */
    int verifyName(String name, String id);

    /**
     * 统计厂商被仪表分类引用的次数
     * @param vendorId 厂商ID
     * @return 引用次数
     */
    int countByVendorId(String vendorId);

    /**
     * 查询所有启用的厂商
     * @return 启用的厂商列表
     */
    List<MtMeterVendorVo> selectAllEnabled();

}
