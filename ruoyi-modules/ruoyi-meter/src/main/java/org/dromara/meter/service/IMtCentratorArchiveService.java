package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtCentratorArchive;
import org.dromara.meter.domain.vo.MtCentratorArchiveVo;

/**
 * 集中器档案 Service 接口
 * 迁移自旧系统 MtCentratorArchiveService
 */
public interface IMtCentratorArchiveService extends IService<MtCentratorArchive> {

    /**
     * 分页查询集中器档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtCentratorArchiveVo> selectPageList(LambdaQueryWrapper<MtCentratorArchive> lqw, PageQuery pageQuery);

}
