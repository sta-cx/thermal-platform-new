package org.sdkj.thermal.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 代理商仪表分配 Mapper
 * 迁移自旧系统 AgentMeterMapper
 * 使用自定义SQL实现跨表关联查询
 */
public interface AgMeterMapper {

    /**
     * 分页查询已分配给公司的电表
     */
    Page<Map<String, Object>> getAllocatedElectricList(Page<?> page, @Param("companyId") String companyId, @Param("search") String search);

    /**
     * 分页查询已分配给公司的水表
     */
    Page<Map<String, Object>> getAllocatedWaterList(Page<?> page, @Param("companyId") String companyId, @Param("search") String search);

    /**
     * 分页查询已分配给公司的热力表
     */
    Page<Map<String, Object>> getAllocatedHeatList(Page<?> page, @Param("companyId") String companyId, @Param("search") String search);

    /**
     * 分页查询已分配给公司的燃气表
     */
    Page<Map<String, Object>> getAllocatedGasList(Page<?> page, @Param("companyId") String companyId, @Param("search") String search);

    /**
     * 查询所有可分配的电表档案
     */
    List<Map<String, Object>> getAllElectricList(@Param("search") String search);

    /**
     * 查询所有可分配的水表档案
     */
    List<Map<String, Object>> getAllWaterList(@Param("search") String search);

    /**
     * 查询所有可分配的热力表档案
     */
    List<Map<String, Object>> getAllHeatList(@Param("search") String search);

    /**
     * 查询所有可分配的燃气表档案
     */
    List<Map<String, Object>> getAllGasList(@Param("search") String search);

    /**
     * 批量插入仪表分配记录
     */
    void insertMeterMatch(@Param("companyId") String companyId,
                          @Param("archiveIds") List<Long> archiveIds,
                          @Param("ids") List<Long> ids,
                          @Param("meterType") String meterType,
                          @Param("username") String username);

    /**
     * 删除该公司该类型的所有分配记录
     */
    void removeMeterMatch(@Param("companyId") String companyId, @Param("meterType") String meterType);
}
