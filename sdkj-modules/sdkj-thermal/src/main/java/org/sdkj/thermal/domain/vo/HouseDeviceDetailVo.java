package org.sdkj.thermal.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 运行监控 - 房屋设备详情 Vo
 */
@Data
public class HouseDeviceDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String roomNum;
    private String phone;
    private String roomTemp;
    private String userName;
    private String code;
    /** 缴费状态: 0=已缴费 1=未缴费 2=停供 3=空置 */
    private Integer paymentStatus;
    private String orgName;
    private String buildingName;
    private String unitCode;
    private String address;

    /** 调节阀列表 */
    private List<DeviceDataVo> heatValveArchiveList;
    /** 开关阀列表 */
    private List<DeviceDataVo> heatCommandValveArchives;
    /** 热表列表 */
    private List<DeviceDataVo> heatHotArchiveList;
    /** 温采器列表 */
    private List<DeviceDataVo> heatTempArchiveList;

    /**
     * 设备通用数据 Vo（调节阀/开关阀/热表/温采器共用）
     */
    @Data
    public static class DeviceDataVo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long id;
        private String meterArcName;
        private String meterNum;
        private String dtuNum;
        private String chanNum;
        private Object settingStatus;
        private Object actualStatus;
        private Object voltage;
        private Object valveStatus;
        private Object signalStrength;
        private Object currentBalance;
        private String cellStatus;
        private String status1;
        private Object temper;
        private Object humidity;
        private Object reportingInterval;
        private Object intervalUnit;
    }
}
