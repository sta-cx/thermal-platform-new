package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sdkj.thermal.domain.PrHeatHotArchive;
import org.sdkj.thermal.domain.PrHeatValveArchive;
import org.sdkj.thermal.domain.PrHouse;
import org.sdkj.thermal.domain.dto.NbValvePayload;
import org.sdkj.thermal.mapper.PrHeatHotArchiveMapper;
import org.sdkj.thermal.mapper.PrHeatValveArchiveMapper;
import org.sdkj.thermal.mapper.PrHouseMapper;
import org.sdkj.thermal.service.IIoTDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * IoT设备数据回调 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoTDataServiceImpl implements IIoTDataService {

    private final PrHeatValveArchiveMapper valveArchiveMapper;
    private final PrHeatHotArchiveMapper hotArchiveMapper;
    private final PrHouseMapper houseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processNbValveData(String timestamp, String imei, String imsi,
                                  String productId, String deviceId, NbValvePayload payload) {
        // 按 meterNum 查找阀门档案 (is_changed = 0)
        PrHeatValveArchive archive = valveArchiveMapper.selectOne(
            new LambdaQueryWrapper<PrHeatValveArchive>()
                .eq(PrHeatValveArchive::getMeterNum, payload.getMeterNum())
                .eq(PrHeatValveArchive::getIsChanged, 0)
        );

        if (archive == null) {
            log.warn("NB阀门数据: 未找到阀门档案 meterNum={}", payload.getMeterNum());
            return 0;
        }

        // 更新阀门档案字段
        archive.setValveStatus(payload.getValveStatus());
        archive.setSettingStatus(payload.getSettingStatus());
        archive.setActualStatus(payload.getActualOpen());
        archive.setInTemperature(payload.getInTemperature());
        archive.setOutTemperature(payload.getOutTemperature());
        archive.setVoltage(payload.getVoltage() != null ? payload.getVoltage().toPlainString() : null);
        archive.setSignalStrength(payload.getCsq());
        archive.setReportingInterval(payload.getReportInterval());
        archive.setIntervalUnit(payload.getIntervalUnit() != null ? String.valueOf(payload.getIntervalUnit()) : null);
        archive.setValidTime(payload.getValidTime());
        archive.setTotalDegree(payload.getTotalDegree());
        archive.setImeiNum(imei);
        archive.setDeviceId(deviceId);
        if (productId != null) {
            archive.setProductId(productId);
        }

        int rows = valveArchiveMapper.updateById(archive);
        log.info("NB阀门数据更新: meterNum={}, rows={}", payload.getMeterNum(), rows);

        // 反写到房屋表
        updateHouseTemperatures(archive.getHouseId(),
            payload.getInTemperature(), payload.getOutTemperature(), payload.getActualOpen());

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processMbusValveData(String meterNum, String valveStatus, String valveOpening,
                                    BigDecimal supplyTemp, BigDecimal returnTemp,
                                    String meterArcCode, String dtuNum, String concentratorCode,
                                    String chanNum, String imei, String deviceId) {
        if ("04310401".equals(meterArcCode)) {
            // 阀门类型 — 更新 pr_heat_valve_archive
            return updateMbusValveArchive(meterNum, valveStatus, valveOpening, supplyTemp, returnTemp);
        } else if ("04030301".equals(meterArcCode)) {
            // 热表类型 — 更新 pr_heat_hot_archive
            return updateMbusHotArchive(meterNum, supplyTemp, returnTemp);
        } else {
            log.warn("Mbus数据: 未知仪表类型 meterArcCode={}, meterNum={}", meterArcCode, meterNum);
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processMobileValveData(String timestamp, String imei, String deviceId, NbValvePayload payload) {
        // 与 NB 阀门相同的数据处理逻辑
        return processNbValveData(timestamp, imei, null, null, deviceId, payload);
    }

    /**
     * 更新 Mbus 阀门档案
     */
    private int updateMbusValveArchive(String meterNum, String valveStatus, String valveOpening,
                                       BigDecimal supplyTemp, BigDecimal returnTemp) {
        PrHeatValveArchive archive = valveArchiveMapper.selectOne(
            new LambdaQueryWrapper<PrHeatValveArchive>()
                .eq(PrHeatValveArchive::getMeterNum, meterNum)
                .eq(PrHeatValveArchive::getIsChanged, 0)
        );

        if (archive == null) {
            log.warn("Mbus阀门数据: 未找到阀门档案 meterNum={}", meterNum);
            return 0;
        }

        archive.setValveStatus(valveStatus);
        Integer parsedOpening = null;
        if (valveOpening != null) {
            try {
                parsedOpening = Integer.parseInt(valveOpening);
                archive.setSettingStatus(parsedOpening);
                archive.setActualStatus(parsedOpening);
            } catch (NumberFormatException e) {
                log.warn("Mbus阀门数据: 阀门开度解析失败 valveOpening={}", valveOpening);
            }
        }
        archive.setInTemperature(supplyTemp);
        archive.setOutTemperature(returnTemp);

        int rows = valveArchiveMapper.updateById(archive);
        log.info("Mbus阀门数据更新: meterNum={}, rows={}", meterNum, rows);

        // 反写到房屋表
        updateHouseTemperatures(archive.getHouseId(), supplyTemp, returnTemp, parsedOpening);

        return rows;
    }

    /**
     * 更新 Mbus 热表档案
     */
    private int updateMbusHotArchive(String meterNum, BigDecimal supplyTemp, BigDecimal returnTemp) {
        PrHeatHotArchive archive = hotArchiveMapper.selectOne(
            new LambdaQueryWrapper<PrHeatHotArchive>()
                .eq(PrHeatHotArchive::getMeterNum, meterNum)
                .eq(PrHeatHotArchive::getIsChanged, 0)
        );

        if (archive == null) {
            log.warn("Mbus热表数据: 未找到热表档案 meterNum={}", meterNum);
            return 0;
        }

        archive.setInTemperature(supplyTemp);
        archive.setOutTemperature(returnTemp);

        int rows = hotArchiveMapper.updateById(archive);
        log.info("Mbus热表数据更新: meterNum={}, rows={}", meterNum, rows);

        // 反写到房屋表 (热表不更新阀门开度)
        updateHouseTemperatures(archive.getHouseId(), supplyTemp, returnTemp, null);

        return rows;
    }

    /**
     * 反写温度数据到房屋表
     */
    private void updateHouseTemperatures(Long houseId, BigDecimal inTemperature,
                                         BigDecimal outTemperature, Integer actualOpen) {
        if (houseId == null) {
            return;
        }
        PrHouse house = houseMapper.selectById(houseId);
        if (house == null) {
            return;
        }
        house.setInTemp(inTemperature);
        house.setOutTemp(outTemperature);
        if (actualOpen != null) {
            house.setValveOpen(actualOpen);
        }
        houseMapper.updateById(house);
    }
}
