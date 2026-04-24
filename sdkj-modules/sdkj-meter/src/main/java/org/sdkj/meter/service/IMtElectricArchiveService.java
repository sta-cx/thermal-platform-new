package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtElectricArchive;
import org.sdkj.meter.domain.vo.MtElectricArchiveVo;

/**
 * 电表档案 Service 接口
 * 迁移自旧系统 MtElectricArchiveService
 */
public interface IMtElectricArchiveService extends IService<MtElectricArchive> {

    /**
     * 分页查询电表档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtElectricArchiveVo> selectPageList(LambdaQueryWrapper<MtElectricArchive> lqw, PageQuery pageQuery);

}
