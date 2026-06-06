package org.sdkj.thermal.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.system.mapper.SysUserMapper;
import org.sdkj.thermal.domain.PrAccountBalance;
import org.sdkj.thermal.domain.PrBuilding;
import org.sdkj.thermal.domain.PrFamily;
import org.sdkj.thermal.domain.PrHeatStationPartition;
import org.sdkj.thermal.domain.PrValveOperationLog;
import org.sdkj.thermal.domain.PrDataGrant;
import org.sdkj.thermal.domain.bo.PrWriteCardLogBo;
import org.sdkj.thermal.domain.dto.PrHeatVo;
import org.sdkj.thermal.mapper.PrAccountBalanceMapper;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
import org.sdkj.thermal.mapper.PrHeatArchiveMapper;
import org.sdkj.thermal.mapper.PrFamilyMapper;
import org.sdkj.thermal.mapper.PrHeatStationMapper;
import org.sdkj.thermal.mapper.PrHeatStationPartitionMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordMapper;
import org.sdkj.thermal.mapper.PrUnitMapper;
import org.sdkj.thermal.mapper.PrValveOperationLogMapper;
import org.sdkj.thermal.mapper.PrTransactionRecordSubMapper;
import org.sdkj.thermal.service.IHtTasksPerformService;
import org.sdkj.thermal.service.IPrOptionsHeatService;
import org.sdkj.thermal.service.OrgAccessService;
import org.sdkj.thermal.service.impl.PrAccountServiceImpl;
import org.sdkj.thermal.service.impl.PrBuildingServiceImpl;
import org.sdkj.thermal.service.impl.PrFamilyServiceImpl;
import org.sdkj.thermal.service.impl.PrHeatArchiveServiceImpl;
import org.sdkj.thermal.service.impl.PrHeatStationPartitionServiceImpl;
import org.sdkj.thermal.service.impl.PrWriteCardLogServiceImpl;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("dev")
class OrgMutationAccessServiceTest {

    @Test
    void buildingSaveRejectsUnauthorizedSubmittedOrgBeforeInsert() throws Exception {
        PrBuildingMapper buildingMapper = mock(PrBuildingMapper.class);
        when(buildingMapper.insert(any(PrBuilding.class))).thenReturn(1);
        PrBuildingServiceImpl service = buildingService(
            buildingMapper, orgAccessServiceWithGrantedOrg("101"));

        PrBuilding building = new PrBuilding();
        building.setOrgId("999");

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, () -> service.save(building));
        }

        verify(buildingMapper, never()).insert(any(PrBuilding.class));
    }

    @Test
    void buildingUpdateRejectsUnauthorizedSubmittedOrgBeforeMapperUpdate() throws Exception {
        PrBuildingMapper buildingMapper = mock(PrBuildingMapper.class);
        when(buildingMapper.updateById(any(PrBuilding.class))).thenReturn(1);
        PrBuildingServiceImpl service = buildingService(
            buildingMapper, orgAccessServiceWithGrantedOrg("101"));

        PrBuilding building = new PrBuilding();
        building.setId(1L);
        building.setOrgId("999");

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, () -> service.updateById(building));
        }

        verify(buildingMapper, never()).updateById(any(PrBuilding.class));
    }

    @Test
    void buildingWrapperUpdateRejectsUnauthorizedSubmittedOrgBeforeMapperUpdate() throws Exception {
        PrBuildingMapper buildingMapper = mock(PrBuildingMapper.class);
        when(buildingMapper.update(any(PrBuilding.class), any())).thenReturn(1);
        PrBuildingServiceImpl service = buildingService(
            buildingMapper, orgAccessServiceWithGrantedOrg("101"));

        PrBuilding building = new PrBuilding();
        building.setOrgId("999");

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class,
                () -> service.update(building, new LambdaUpdateWrapper<PrBuilding>().eq(PrBuilding::getId, 1L)));
        }

        verify(buildingMapper, never()).update(any(PrBuilding.class), any());
    }

    @Test
    void writeCardLogRejectsUnauthorizedOrgBeforeInsert() {
        PrValveOperationLogMapper logMapper = mock(PrValveOperationLogMapper.class);
        PrWriteCardLogServiceImpl service = new PrWriteCardLogServiceImpl(
            logMapper,
            mock(PrHouseMapper.class),
            mock(PrHeatValveArchiveMapper.class),
            mock(PrCompanyMapper.class),
            mock(SysUserMapper.class),
            orgAccessServiceWithGrantedOrg("101")
        );

        PrWriteCardLogBo bo = new PrWriteCardLogBo();
        bo.setOrgId("999");
        bo.setMeterId(1L);
        bo.setType("1");

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, () -> service.insertWriteCardLog(bo));
        }

        verify(logMapper, never()).insert(any(PrValveOperationLog.class));
    }

    @Test
    void familySaveRejectsInaccessibleHouseBeforeInsert() {
        PrFamilyMapper familyMapper = mock(PrFamilyMapper.class);
        PrFamilyServiceImpl service = new PrFamilyServiceImpl(familyMapper, mock(PrHouseMapper.class));

        PrFamily family = new PrFamily();
        family.setHouseId(99L);

        assertThrows(ServiceException.class, () -> service.save(family));

        verify(familyMapper, never()).insert(any(PrFamily.class));
    }

    @Test
    void stationPartitionSaveRejectsInaccessibleStationBeforeInsert() {
        PrHeatStationPartitionMapper partitionMapper = mock(PrHeatStationPartitionMapper.class);
        PrHeatStationPartitionServiceImpl service = new PrHeatStationPartitionServiceImpl(
            partitionMapper, mock(PrHeatStationMapper.class));

        PrHeatStationPartition partition = new PrHeatStationPartition();
        partition.setStationId(99L);

        assertThrows(ServiceException.class, () -> service.save(partition));

        verify(partitionMapper, never()).insert(any(PrHeatStationPartition.class));
    }

    @Test
    void accountOpenRejectsInaccessibleHouseBeforeInsert() {
        PrAccountBalanceMapper balanceMapper = mock(PrAccountBalanceMapper.class);
        PrAccountServiceImpl service = new PrAccountServiceImpl(
            balanceMapper,
            mock(PrTransactionRecordMapper.class),
            mock(PrHouseMapper.class)
        );

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class,
                () -> service.insertData(List.of(99L), "1", "101", "1"));
        }

        verify(balanceMapper, never()).insert(any(PrAccountBalance.class));
    }

    @Test
    void heatArchiveManualControlRejectsUnauthorizedOrgBeforeSavingCommands() throws Exception {
        IHtTasksPerformService tasksPerformService = mock(IHtTasksPerformService.class);
        PrHeatArchiveServiceImpl service = heatArchiveService(
            tasksPerformService, orgAccessServiceWithGrantedOrg("101"));

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            ServiceException ex = assertThrows(ServiceException.class,
                () -> service.manualControl(List.of(new PrHeatVo()), true, 1, "1", "999", null, null, null));
            assertTrue(ex.getMessage().contains("无权操作组织数据: 999"));
        }

        verify(tasksPerformService, never()).saveBatchTasks(any());
    }

    @Test
    void heatArchiveXunceRejectsUnauthorizedOrgBeforeSavingCommands() throws Exception {
        IHtTasksPerformService tasksPerformService = mock(IHtTasksPerformService.class);
        PrHeatArchiveServiceImpl service = heatArchiveService(
            tasksPerformService, orgAccessServiceWithGrantedOrg("101"));

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            ServiceException ex = assertThrows(ServiceException.class,
                () -> service.xunce(List.of(new PrHeatVo()), "999"));
            assertTrue(ex.getMessage().contains("无权操作组织数据: 999"));
        }

        verify(tasksPerformService, never()).saveBatchTasks(any());
    }

    @Test
    void heatArchiveSetValveGroupRejectsUnauthorizedOrgBeforeSavingCommands() throws Exception {
        IHtTasksPerformService tasksPerformService = mock(IHtTasksPerformService.class);
        PrHeatArchiveServiceImpl service = heatArchiveService(
            tasksPerformService, orgAccessServiceWithGrantedOrg("101"));

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            ServiceException ex = assertThrows(ServiceException.class,
                () -> service.setValveGroupParam(List.of(new PrHeatVo()), "1", "999"));
            assertTrue(ex.getMessage().contains("无权操作组织数据: 999"));
        }

        verify(tasksPerformService, never()).saveBatchTasks(any());
    }

    private static PrBuildingServiceImpl buildingService(
        PrBuildingMapper buildingMapper, OrgAccessService orgAccessService) throws Exception {
        PrBuildingServiceImpl service = new PrBuildingServiceImpl(
            buildingMapper,
            mock(PrUnitMapper.class),
            mock(PrHouseMapper.class),
            mock(PrCompanyMapper.class)
        );
        setMybatisPlusBaseMapper(service, buildingMapper);
        setOrgAccessServiceIfPresent(service, orgAccessService);
        return service;
    }

    private static PrHeatArchiveServiceImpl heatArchiveService(
        IHtTasksPerformService tasksPerformService, OrgAccessService orgAccessService) throws Exception {
        PrHeatArchiveMapper archiveMapper = mock(PrHeatArchiveMapper.class);
        PrHeatArchiveServiceImpl service = new PrHeatArchiveServiceImpl(
            archiveMapper,
            tasksPerformService,
            mock(IPrOptionsHeatService.class),
            mock(PrTransactionRecordMapper.class),
            mock(PrTransactionRecordSubMapper.class)
        );
        setMybatisPlusBaseMapper(service, archiveMapper);
        setOrgAccessServiceIfPresent(service, orgAccessService);
        return service;
    }

    private static void setMybatisPlusBaseMapper(ServiceImpl<?, ?> service, Object mapper) throws Exception {
        Class<?> type = service.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if ("baseMapper".equals(field.getName())) {
                    field.setAccessible(true);
                    field.set(service, mapper);
                }
            }
            type = type.getSuperclass();
        }
    }

    private static void setOrgAccessServiceIfPresent(Object service, OrgAccessService orgAccessService)
        throws IllegalAccessException {
        Class<?> type = service.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getType() == OrgAccessService.class) {
                    field.setAccessible(true);
                    field.set(service, orgAccessService);
                    return;
                }
            }
            type = type.getSuperclass();
        }
    }

    private static OrgAccessService orgAccessServiceWithGrantedOrg(String orgId) {
        PrDataGrant grant = new PrDataGrant();
        grant.setUserId(7L);
        grant.setOrgId(orgId);

        PrDataGrantMapper mapper = mock(PrDataGrantMapper.class);
        when(mapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(grant));
        return new OrgAccessService(mapper);
    }

    private static MockedStatic<LoginHelper> mockCurrentUser() {
        MockedStatic<LoginHelper> login = mockStatic(LoginHelper.class);
        login.when(LoginHelper::isSuperAdmin).thenReturn(false);
        login.when(LoginHelper::isTenantAdmin).thenReturn(false);
        login.when(LoginHelper::getUserId).thenReturn(7L);
        login.when(LoginHelper::getUserIdStr).thenReturn("7");
        return login;
    }
}
