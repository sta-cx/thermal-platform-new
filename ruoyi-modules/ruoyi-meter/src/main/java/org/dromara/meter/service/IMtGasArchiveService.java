package org.dromara.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.meter.domain.MtGasArchive;
import org.dromara.meter.domain.vo.MtGasArchiveVo;

/**
 * 燃气表档案 Service 接口
 * 迁移自旧系统 MtGasArchiveService
 */
public interface IMtGasArchiveService extends IService<MtGasArchive> {

    /**
     * 分页查询燃气表档案列表
     * @param lqw 查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<MtGasArchiveVo> selectPageList(LambdaQueryWrapper<MtGasArchive> lqw, PageQuery pageQuery);

}
