package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtTcArchive;
import org.dromara.meter.domain.vo.MtTcArchiveVo;

import java.util.List;

/**
 * 温控器档案 Service 接口
 * 迁移自旧系统 MtTcArchiveService
 */
public interface IMtTcArchiveService extends IService<MtTcArchive> {

    /**
     * 分页查询温控器档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtTcArchiveVo> selectPageList(LambdaQueryWrapper<MtTcArchive> lqw, PageQuery pageQuery);

    /**
     * 查询所有启用的温控器列表
     * @param lqw 查询条件
     * @return 温控器列表
     */
    List<MtTcArchiveVo> selectList(LambdaQueryWrapper<MtTcArchive> lqw);

}
