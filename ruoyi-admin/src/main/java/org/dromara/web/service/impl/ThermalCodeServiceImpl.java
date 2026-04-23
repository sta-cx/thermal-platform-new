package org.dromara.web.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThermalCodeServiceImpl implements ThermalCodeService {

    private final RedissonClient redissonClient;

    private String nextCode(String type, String sortId) {
        String key = "thermal:code:" + type + ":" + sortId;
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        long next = atomicLong.incrementAndGet();
        return "0" + next;
    }

    @Override
    public String accessMtVendorCode() {
        return nextCode("vendor", "0");
    }

    @Override
    public String accessMtSortCode(String vendorId, String meterType, String vendorCode) {
        String sortCode = nextCode("sort", vendorId + ":" + meterType);
        return vendorCode + meterType + sortCode;
    }

    @Override
    public String accessEleCode(String sortId, String sortCode) {
        return sortCode + nextCode("ele", sortId);
    }

    @Override
    public String accessWaterCode(String sortCode, String sortId) {
        return sortCode + nextCode("water", sortId);
    }

    @Override
    public String accessHotCode(String sortCode, String sortId) {
        return sortCode + nextCode("hot", sortId);
    }

    @Override
    public String accessGasCode(String sortCode, String sortId) {
        return sortCode + nextCode("gas", sortId);
    }

    @Override
    public String accessValveCode(String sortCode, String sortId) {
        return sortCode + nextCode("valve", sortId);
    }

    @Override
    public String accessTempCode(String sortCode, String sortId) {
        return sortCode + nextCode("temp", sortId);
    }
}
