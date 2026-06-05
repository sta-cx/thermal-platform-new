package org.sdkj.thermal.permission;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.common.mybatis.annotation.OrgPermission;
import org.sdkj.thermal.mapper.HtScopeDtuMapper;
import org.sdkj.thermal.mapper.HtTasksPerformMapper;
import org.sdkj.thermal.mapper.PrExpenseItemMapper;
import org.sdkj.thermal.mapper.PrRepairPersonMapper;
import org.sdkj.thermal.mapper.PrSchedulingMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("dev")
class OrgPermissionMapperCoverageTest {

    @Test
    void exposedOrgScopedMappersHaveOrgPermission() {
        assertOrgPermission(HtTasksPerformMapper.class);
        assertOrgPermission(HtScopeDtuMapper.class);
        assertOrgPermission(PrRepairPersonMapper.class);
        assertOrgPermission(PrSchedulingMapper.class);
        assertOrgPermission(PrValveOperationLogMapper.class);
        assertOrgPermission(PrExpenseItemMapper.class);
    }

    private void assertOrgPermission(Class<?> mapperClass) {
        assertNotNull(
            mapperClass.getAnnotation(OrgPermission.class),
            mapperClass.getSimpleName() + " must be annotated with @OrgPermission"
        );
    }
}
