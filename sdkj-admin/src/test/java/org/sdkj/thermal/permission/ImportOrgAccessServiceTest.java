package org.sdkj.thermal.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.common.satoken.utils.LoginHelper;
import org.sdkj.thermal.domain.PrDataGrant;
import org.sdkj.thermal.domain.SysOrganization;
import org.sdkj.thermal.mapper.PrBuildingMapper;
import org.sdkj.thermal.mapper.PrCompanyMapper;
import org.sdkj.thermal.mapper.PrDataGrantMapper;
import org.sdkj.thermal.mapper.PrImportHistoryMapper;
import org.sdkj.thermal.mapper.PrImportRecordMapper;
import org.sdkj.thermal.service.OrgAccessService;
import org.sdkj.thermal.service.impl.PrCompanyServiceImpl;
import org.sdkj.thermal.service.impl.PrImportHistoryServiceImpl;
import org.sdkj.thermal.service.impl.PrImportRecordServiceImpl;

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
class ImportOrgAccessServiceTest {

    @Test
    void transactionRecordImportSubmitRejectsUnauthorizedResolvedOrgIds() {
        PrImportRecordMapper mapper = mock(PrImportRecordMapper.class);
        OrgAccessService orgAccessService = orgAccessServiceWithGrantedOrg("101");
        PrImportRecordServiceImpl service = new PrImportRecordServiceImpl(mapper, orgAccessService);

        when(mapper.selectImportedOrgIds("7")).thenReturn(List.of("999"));

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, service::submitData);
        }

        verify(mapper, never()).submitData(any());
        verify(mapper, never()).deleteImportRecordData(any());
    }

    @Test
    void historyImportSubmitRejectsUnauthorizedResolvedOrgIds() {
        PrImportHistoryMapper mapper = mock(PrImportHistoryMapper.class);
        OrgAccessService orgAccessService = orgAccessServiceWithGrantedOrg("101");
        PrImportHistoryServiceImpl service = new PrImportHistoryServiceImpl(mapper, orgAccessService);

        when(mapper.selectImportedOrgIds("7")).thenReturn(List.of("999"));

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, service::submitData);
        }

        verify(mapper, never()).submitData(any());
        verify(mapper, never()).deleteImportHistoryData(any());
    }

    @Test
    void organizationCascadeDeleteRejectsUnauthorizedOrgBeforeDeletingData() {
        PrCompanyMapper companyMapper = mock(PrCompanyMapper.class);
        PrBuildingMapper buildingMapper = mock(PrBuildingMapper.class);
        PrDataGrantMapper dataGrantMapper = dataGrantMapperWithGrantedOrg("101");
        OrgAccessService orgAccessService = new OrgAccessService(dataGrantMapper);
        PrCompanyServiceImpl service = new PrCompanyServiceImpl(
            companyMapper, buildingMapper, dataGrantMapper, orgAccessService);
        SysOrganization org = new SysOrganization();
        org.setId("999");
        org.setCompanyId("1");
        org.setLevel("2");
        when(companyMapper.selectOrgById("999")).thenReturn(org);

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            assertThrows(ServiceException.class, () -> service.deleteAllData("999"));
        }

        verify(companyMapper, never()).deleteValveData(any());
        verify(companyMapper, never()).deleteBuildingData(any());
        verify(companyMapper, never()).deleteOrgById(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void clearUserOrgForNonPrivilegedUserOnlyDeletesGrantsCreatedByCurrentUser() {
        PrCompanyMapper companyMapper = mock(PrCompanyMapper.class);
        PrBuildingMapper buildingMapper = mock(PrBuildingMapper.class);
        PrDataGrantMapper dataGrantMapper = mock(PrDataGrantMapper.class);
        OrgAccessService orgAccessService = new OrgAccessService(dataGrantMapper);
        PrCompanyServiceImpl service = new PrCompanyServiceImpl(
            companyMapper, buildingMapper, dataGrantMapper, orgAccessService);

        try (MockedStatic<LoginHelper> login = mockCurrentUser()) {
            service.clearUserOrg(88L);
        }

        ArgumentCaptor<LambdaQueryWrapper<PrDataGrant>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(dataGrantMapper).delete(captor.capture());
        int conditionSegmentCount = captor.getValue().getExpression().getNormal().size();
        assertTrue(conditionSegmentCount > 3, "clearUserOrg must include user_id and create_by filters");
    }

    private static OrgAccessService orgAccessServiceWithGrantedOrg(String orgId) {
        return new OrgAccessService(dataGrantMapperWithGrantedOrg(orgId));
    }

    private static PrDataGrantMapper dataGrantMapperWithGrantedOrg(String orgId) {
        PrDataGrant grant = new PrDataGrant();
        grant.setUserId(7L);
        grant.setOrgId(orgId);

        PrDataGrantMapper mapper = mock(PrDataGrantMapper.class);
        when(mapper.selectList(any())).thenReturn(List.of(grant));
        return mapper;
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
