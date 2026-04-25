package org.sdkj.thermal.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ag_reader_param")
public class AgReaderParam {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String code;
    private Integer icIsstart;
    private String icType;
    private Integer icPort;
    private Integer icBaud;
    private Integer dpIsstart;
    private String dpType;
    private Integer dpPort;
    private Integer dpBaud;
    private Integer idIsstart;
    private String idType;
    private Integer idPort;
    private Integer idBaud;
    private Integer recogIsstart;
    private String recogType;
    private Integer recogPort;
    private Integer recogBaud;
    private Integer isAutoUpdate;
    private String scanTime;
    private Integer scanInterval;
    private String userName;
    private String userPwd;
    private Integer isReadWater;
    private Integer isReadEle;
    private Integer isReceiptPrinter;
    private String exchangeServiceIp;
    private Integer isAutoRestart;
}
