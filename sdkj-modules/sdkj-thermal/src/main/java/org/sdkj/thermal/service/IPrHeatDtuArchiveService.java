package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.core.domain.R;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.bo.PrHeatDtuArchiveBo;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * DTU采集器配表 Service 接口
 */
public interface IPrHeatDtuArchiveService extends IService<PrHeatDtuArchive> {

    /**
     * 根据ID查询档案详情
     */
    PrHeatDtuArchiveVo selectById(Serializable id);

    /**
     * 分页查询DTU采集器配表列表
     * @param orgId 小区ID
     * @param search 搜索关键字（DTU编号）
     * @param status 状态
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatDtuArchiveVo> selectPageList(String orgId, String search,
                                                      String status, PageQuery pageQuery);

    /**
     * 查询DTU下所有仪表信息并生成查询指令
     * @param bo DTU档案信息
     * @return 是否成功
     */
    Boolean queryMeter(PrHeatDtuArchiveBo bo);

    // ========== 批量操作 ==========

    /**
     * 查询全部DTU档案信息（用于导出）
     * @param orgId 小区ID
     * @return DTU档案列表
     */
    List<PrHeatDtuArchiveVo> listAll(String orgId);

    /**
     * 导入DTU采集器配表
     * @param file Excel 文件
     * @return 导入结果
     */
    R<Void> importDtuArchive(MultipartFile file) throws IOException;
}
