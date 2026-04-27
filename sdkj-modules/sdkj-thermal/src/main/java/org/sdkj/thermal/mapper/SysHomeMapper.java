package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页数据大屏 Mapper
 * SQL 映射从旧系统 SysHomeMapper.xml 移植
 */
public interface SysHomeMapper {

    // Block 1: 基础统计
    String queryOrgNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryOrgStationNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryBuildingNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryAllBuildingNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryStationPartitionNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHouseNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryPrUserNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHeatingArea(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHeatingAreaSum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 2: 阀门/户表统计
    String queryValveNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryValveNumL(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryUnitValveNumL(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryHotArchiveNumL(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 3: 单元表/温采器/DTU
    String queryUnitHotArchiveNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryUnitHotArchiveNumL(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryTempArchiveNumL(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryDtuNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryDtuNumZ(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 4: 温度/热量统计
    String queryValveArchiveOut(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveArchiveIn(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveArchiveActualStatus(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveOut(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveIn(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveActualStatus(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveTemper(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveTotal(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveTotal(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveTotalAll(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveTotalAll(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 6: 换热站数据
    String queryStationData(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 7: 缴费统计
    String queryJFNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryQFNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryKZNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTGNum(@Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
}
