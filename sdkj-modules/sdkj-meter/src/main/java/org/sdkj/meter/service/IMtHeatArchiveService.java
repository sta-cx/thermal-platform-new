package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;

/**
 * 热力表档案 Service 接口
 * 迁移自旧系统 MtHeatArchiveService
 */
public interface IMtHeatArchiveService extends IService<MtHeatArchive> {

    /**
     * 分页查询热力表档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtHeatArchiveVo> selectPageList(LambdaQueryWrapper<MtHeatArchive> lqw, PageQuery pageQuery);

}
