package org.sdkj.thermal.permission;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.mapper.HtScopeDtuMapper;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.mapper.PrAccountBalanceMapper;
import org.sdkj.thermal.mapper.PrExpenseItemMapper;
import org.sdkj.thermal.mapper.PrHeatRealDataMapper;
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.mapper.PrHouseLogMapper;
import org.sdkj.thermal.mapper.PrOptionsHeatMapper;
import org.sdkj.thermal.mapper.PrRepairPersonMapper;
import org.sdkj.thermal.mapper.PrSchedulingMapper;
import org.sdkj.thermal.mapper.PrStrategyMapper;
import org.sdkj.thermal.mapper.PrUnitMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 验证所有有 orgId 字段且暴露给前端的 Mapper 都标注了 @OrgPermission。
 * 此测试确保 SQL 级别的 SELECT/UPDATE/DELETE 保护到位。
 */
@Tag("dev")
class OrgPermissionMapperCoverageTest {

    // ---- 已有 6 个（原始基线） ----

    @Test
    void exposedOrgScopedMappersHaveOrgPermission() {
        assertOrgPermission(HtTasksPerformMapper.class);
        assertOrgPermission(HtScopeDtuMapper.class);
        assertOrgPermission(PrRepairPersonMapper.class);
        assertOrgPermission(PrSchedulingMapper.class);
        assertOrgPermission(PrValveOperationLogMapper.class);
        assertOrgPermission(PrExpenseItemMapper.class);
    }

    // ---- 新增：补充覆盖其余有 @OrgPermission 的 Mapper ----

    @Test
    void accountBalanceMapperHasOrgPermission() {
        assertOrgPermission(PrAccountBalanceMapper.class);
    }

    @Test
    void heatRealDataMapperHasOrgPermission() {
        assertOrgPermission(PrHeatRealDataMapper.class);
    }

    @Test
    void heatStationMapperHasOrgPermission() {
        assertOrgPermission(PrHeatStationMapper.class);
    }

    @Test
    void houseLogMapperHasOrgPermission() {
        assertOrgPermission(PrHouseLogMapper.class);
    }

    @Test
    void optionsHeatMapperHasOrgPermission() {
        assertOrgPermission(PrOptionsHeatMapper.class);
    }

    @Test
    void strategyMapperHasOrgPermission() {
        assertOrgPermission(PrStrategyMapper.class);
    }

    @Test
    void unitMapperHasOrgPermission() {
        assertOrgPermission(PrUnitMapper.class);
    }

    private void assertOrgPermission(Class<?> mapperClass) {
        assertNotNull(
            mapperClass.getAnnotation(OrgPermission.class),
            mapperClass.getSimpleName() + " must be annotated with @OrgPermission"
        );
    }
}
