package org.sdkj.thermal.service.impl;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("dev")
class PrImportBasicDataServiceImplTest {

    @Test
    void normalizePaymentStatusMapsImportLabelsToHouseStatusCodes() {
        assertEquals(1, PrImportBasicDataServiceImpl.normalizePaymentStatus("已缴费"));
        assertEquals(0, PrImportBasicDataServiceImpl.normalizePaymentStatus("未缴费"));
        assertEquals(2, PrImportBasicDataServiceImpl.normalizePaymentStatus("停供"));
        assertEquals(3, PrImportBasicDataServiceImpl.normalizePaymentStatus("空置"));
        assertEquals(1, PrImportBasicDataServiceImpl.normalizePaymentStatus("1"));
        assertNull(PrImportBasicDataServiceImpl.normalizePaymentStatus(" "));
    }

    @Test
    void normalizePaymentStatusRejectsUnknownLabels() {
        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> PrImportBasicDataServiceImpl.normalizePaymentStatus("已停暖")
        );

        assertEquals("缴费状态只能填写：已缴费、未缴费、停供、空置", error.getMessage());
    }
}
