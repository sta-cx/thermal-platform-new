package org.sdkj.web.service;

public interface ThermalCodeService {

    String accessMtVendorCode();

    String accessMtSortCode(String vendorId, String meterType, String vendorCode);

    String accessEleCode(String sortId, String sortCode);

    String accessWaterCode(String sortCode, String sortId);

    String accessHotCode(String sortCode, String sortId);

    String accessGasCode(String sortCode, String sortId);

    String accessValveCode(String sortCode, String sortId);

    String accessTempCode(String sortCode, String sortId);
}
