package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.dto.NbValvePayload;

import java.math.BigDecimal;

/**
 * IoT设备数据回调 Service 接口
 */
public interface IIoTDataService {

    /**
     * 处理电信NB阀门上报数据
     *
     * @param timestamp 推送时间(已格式化)
     * @param imei      IMEI号
     * @param imsi      IMSI号
     * @param productId 产品ID
     * @param deviceId  设备ID
     * @param payload   解析后的阀门数据
     * @return 更新记录数
     */
    int processNbValveData(String timestamp, String imei, String imsi,
                           String productId, String deviceId, NbValvePayload payload);

    /**
     * 处理世达Mbus阀门/热表上报数据
     *
     * @param meterNum          仪表编号(valveNo)
     * @param valveStatus       阀门状态
     * @param valveOpening      阀门开度
     * @param supplyTemp        进水温度
     * @param returnTemp        回水温度
     * @param meterArcCode      仪表档案编码("04310401"=阀门, "04030301"=热表)
     * @param dtuNum            DTU编号
     * @param concentratorCode  集中器编码
     * @param chanNum           通道号
     * @param imei              IMEI号
     * @param deviceId          设备ID
     * @return 更新记录数
     */
    int processMbusValveData(String meterNum, String valveStatus, String valveOpening,
                             BigDecimal supplyTemp, BigDecimal returnTemp,
                             String meterArcCode, String dtuNum, String concentratorCode,
                             String chanNum, String imei, String deviceId);

    /**
     * 处理移动平台NB阀门上报数据
     *
     * @param timestamp 推送时间(已格式化)
     * @param imei      IMEI号
     * @param deviceId  设备ID
     * @param payload   解析后的阀门数据
     * @return 更新记录数
     */
    int processMobileValveData(String timestamp, String imei, String deviceId, NbValvePayload payload);
}
