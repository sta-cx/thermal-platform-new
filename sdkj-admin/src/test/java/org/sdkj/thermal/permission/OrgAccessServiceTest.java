package org.sdkj.thermal.permission;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sdkj.common.core.exception.ServiceException;
import org.sdkj.thermal.service.OrgAccessService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("dev")
class OrgAccessServiceTest {

    @Test
    void noGrantRowsAllowAllOrgIdsToMatchOrgPermissionInterceptorSemantics() {
        assertDoesNotThrow(() -> OrgAccessService.assertOrgIdsAllowed(
            List.of("101", "102"), Set.of()));
    }

    @Test
    void existingGrantRowsRejectUnauthorizedOrgIds() {
        ServiceException error = assertThrows(ServiceException.class, () ->
            OrgAccessService.assertOrgIdsAllowed(List.of("101", "999"), Set.of("101")));

        assertTrue(error.getMessage().contains("999"), error.getMessage());
    }

    @Test
    void blankOrgIdsAreIgnoredBecauseTheyAreHandledByImportValidation() {
        assertDoesNotThrow(() -> OrgAccessService.assertOrgIdsAllowed(
            List.of("101", "", "  "), Set.of("101")));
    }
}
