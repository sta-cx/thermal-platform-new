package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatTempArchive;
import org.sdkj.thermal.domain.vo.PrHeatTempArchiveVo;

import java.io.Serializable;
import java.util.List;

/**
 * 温采器配表 Service 接口
 */
public interface IPrHeatTempArchiveService extends IService<PrHeatTempArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatTempArchiveVo selectById(Serializable id);

    /**
     * 分页查询温采器配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（房屋ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatTempArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                       String unit, String search, String parentId,
                                                       PageQuery pageQuery);

    // ========== 批量操作 ==========

    /**
     * 同步户温采器信息到采集平台
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 是否同步成功
     */
    boolean valveInformationSynchronization(String orgId, String companyId);

    /**
     * 获取同步数据列表（用于下载Excel）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 温采器配表列表
     */
    List<PrHeatTempArchiveVo> listSyncData(String companyId, String orgId);

    /**
     * 查询全部温采器信息（用于导出）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 温采器配表列表
     */
    List<PrHeatTempArchiveVo> listAll(String companyId, String orgId);
}
