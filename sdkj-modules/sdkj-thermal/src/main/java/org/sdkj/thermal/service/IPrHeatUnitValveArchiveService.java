package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatUnitValveArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitValveArchiveVo;

import java.io.Serializable;

/**
 * 单元阀门配表 Service 接口
 */
public interface IPrHeatUnitValveArchiveService extends IService<PrHeatUnitValveArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatUnitValveArchiveVo selectById(Serializable id);

    /**
     * 分页查询单元阀门配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（单元ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatUnitValveArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                            String unit, String search, String parentId,
                                                            PageQuery pageQuery);
}
