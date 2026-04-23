package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtHeatArchive;
import org.dromara.meter.domain.vo.MtHeatArchiveVo;

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
