package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.vo.PrHeatHotArchiveVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

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
     * @param orgId 小区ID
     * @param buildingId 楼宇ID
     * @param unit 单元
     * @param search 搜索关键字（表号/档案名称）
     * @param parentId 父级ID（房屋ID）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatHotArchiveVo> selectPageList(String orgId, String buildingId,
                                                      String unit, String search, String parentId,
                                                      PageQuery pageQuery);

    // ========== 批量操作 ==========

    /**
     * 同步户热表信息到采集平台
     * @param orgId 小区ID
     * @return 是否同步成功
     */
    boolean valveInformationSynchronization(String orgId);

    /**
     * 获取同步数据列表（用于下载Excel）
     * @param orgId 小区ID
     * @return 热量表配表列表
     */
    List<PrHeatHotArchiveVo> listSyncData(String orgId);

    /**
     * 查询全部热量表信息（用于导出）
     * @param orgId 小区ID
     * @return 热量表配表列表
     */
    List<PrHeatHotArchiveVo> listAll(String orgId);

    /**
     * 导入热量表配表
     * @param file Excel 文件
     * @return 导入结果
     */
    R<Void> importHotArchive(MultipartFile file) throws IOException;
}
