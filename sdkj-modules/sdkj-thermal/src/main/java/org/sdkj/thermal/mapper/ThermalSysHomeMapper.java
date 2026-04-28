package org.sdkj.thermal.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 首页数据大屏 Mapper
 * SQL 映射从旧系统 SysHomeMapper.xml 移植
 */
public interface ThermalSysHomeMapper {

    // Block 1: 基础统计
    String queryOrgNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryOrgStationNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryBuildingNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryAllBuildingNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryStationPartitionNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHouseNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryPrUserNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHeatingArea(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHeatingAreaSum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 2: 阀门/户表统计
    String queryValveNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryValveNumL(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryUnitValveNumL(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryHotArchiveNumL(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 3: 单元表/温采器/DTU
    String queryUnitHotArchiveNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryUnitHotArchiveNumL(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    List<Map<String, Integer>> queryTempArchiveNumL(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryDtuNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryDtuNumZ(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 4: 温度/热量统计
    String queryValveArchiveOut(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveArchiveIn(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryValveArchiveActualStatus(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveOut(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveIn(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitValveArchiveActualStatus(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTempArchiveTemper(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveTotal(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveTotal(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryHotArchiveTotalAll(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryUnitHotArchiveTotalAll(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 6: 换热站数据
    String queryStationData(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);

    // Block 7: 缴费统计
    String queryJFNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryQFNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryKZNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
    String queryTGNum(@Param("userId") Long userId, @Param("companyId") String companyId, @Param("stationId") String stationId, @Param("stationPartitionId") String stationPartitionId);
}
