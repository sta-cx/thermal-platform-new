package org.sdkj.thermal.service;

import org.sdkj.common.mybatis.core.page.PageQuery;
import org.sdkj.common.mybatis.core.page.TableDataInfo;

import java.util.List;
import java.util.Map;

/**
 * 代理商仪表分配 Service 接口
 */
public interface IAgMeterService {

    /**
     * 分页查询已分配给指定公司的仪表
     *
     * @param companyId 公司ID
     * @param meterType 仪表类型 (01=电表, 02=水表, 03=热力表, 04=燃气表)
     * @param search    搜索关键字（编号或名称）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<Map<String, Object>> queryAllocatedMeters(String companyId, String meterType, String search, PageQuery pageQuery);

    /**
     * 查询所有可分配的仪表档案（全量列表）
     *
     * @param meterType 仪表类型 (01=电表, 02=水表, 03=热力表, 04=燃气表)
     * @param search    搜索关键字（编号或名称）
     * @return 仪表档案列表
     */
    List<Map<String, Object>> queryAllMeters(String meterType, String search);

    /**
     * 批量分配仪表给公司（事务操作：先删除该公司该类型的旧分配记录，再插入新记录）
     *
     * @param companyId  公司ID
     * @param archiveIds 逗号分隔的档案ID
     * @param meterType  仪表类型 (01=电表, 02=水表, 03=热力表, 04=燃气表)
     */
    void allocateMeters(String companyId, String archiveIds, String meterType);
}
