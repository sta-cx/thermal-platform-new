package org.sdkj.thermal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;
import org.sdkj.thermal.domain.PrHeatDtuArchive;
import org.sdkj.thermal.domain.vo.PrHeatDtuArchiveVo;

import java.io.Serializable;

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
     * @param companyId 公司ID
     * @param orgId 小区ID
     * @param search 搜索关键字（DTU编号）
     * @param status 状态
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<PrHeatDtuArchiveVo> selectPageList(String companyId, String orgId, String search,
                                                      String status, PageQuery pageQuery);
}
