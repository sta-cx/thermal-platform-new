package org.sdkj.meter.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.meter.domain.MtHeatArchive;
import org.sdkj.meter.domain.vo.MtHeatArchiveVo;

import java.util.List;

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

    /**
     * 获取所有热力表列表（不分页）
     * 对应旧系统 getHeatList() 方法
     * @return 所有热力表列表
     */
    List<MtHeatArchiveVo> getHeatList();

    /**
     * 按条件查询热力表
     * 对应旧系统 queryMtHeatArchive() 方法
     * @param entity 查询条件实体
     * @return 热力表列表
     */
    List<MtHeatArchiveVo> queryMtHeatArchive(MtHeatArchive entity);

}
