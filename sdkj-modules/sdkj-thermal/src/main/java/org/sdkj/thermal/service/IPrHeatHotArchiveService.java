package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;

import java.io.Serializable;

/**
 * 房屋热量表配表 Service 接口
 */
public interface IPrHeatHotArchiveService extends IService<PrHeatHotArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatHotArchiveVo selectById(Serializable id);

    /**
     * 分页查询房屋热量表配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（房屋ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatHotArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                      String unit, String search, String parentId,
                                                      PageQuery pageQuery);
}
