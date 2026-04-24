package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatArchive;
import org.sdkj.thermal.domain.vo.PrHeatArchiveVo;

import java.io.Serializable;
import java.util.List;

/**
 * 房屋热表配表 Service 接口
 */
public interface IPrHeatArchiveService extends IService<PrHeatArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatArchiveVo selectById(Serializable id);

    /**
     * 分页查询房屋热表配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unitCode 单元编码
     * @param search 搜索关键字（表号/档案名称）
     * @param archiveId 档案ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                   String unitCode, String search, String archiveId,
                                                   PageQuery pageQuery);

    /**
     * 查询公司下所有热表档案
     * @param companyId 公司ID
     * @return 热表档案列表
     */
    List<PrHeatArchiveVo> queryCompanyHeat(String companyId);
}
