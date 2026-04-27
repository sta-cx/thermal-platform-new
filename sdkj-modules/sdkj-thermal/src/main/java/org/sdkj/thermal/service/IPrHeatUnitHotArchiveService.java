package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatUnitHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatUnitHotArchiveVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 单元热表配表 Service 接口
 */
public interface IPrHeatUnitHotArchiveService extends IService<PrHeatUnitHotArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatUnitHotArchiveVo selectById(Serializable id);

    /**
     * 分页查询单元热表配表列表
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（单元ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatUnitHotArchiveVo> selectPageList(String companyId, String orgId, String buildingId,
                                                          String unit, String search, String parentId,
                                                          PageQuery pageQuery);

    // ========== 批量操作 ==========

    /**
     * 同步单元热表信息到采集平台
     * @param orgId 小区ID
     * @param companyId 公司ID
     * @return 是否同步成功
     */
    boolean valveInformationSynchronization(String orgId, String companyId);

    /**
     * 获取同步数据列表（用于下载Excel）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 单元热表配表列表
     */
    List<PrHeatUnitHotArchiveVo> listSyncData(String companyId, String orgId);

    /**
     * 查询全部单元热表信息（用于导出）
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @return 单元热表配表列表
     */
    List<PrHeatUnitHotArchiveVo> listAll(String companyId, String orgId);

    /**
     * 导入单元热表配表
     * @param file Excel 文件
     * @return 导入结果
     */
    R<Void> importUnitHotArchive(MultipartFile file) throws IOException;
}
