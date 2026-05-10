package org.sdkj.thermal.service;

import org.sdkj.thermal.domain.vo.PrTransactionRecordVo;

/**
 * 自助缴费机 Service 接口
 */
public interface IPrAutoMachineService {

    /**
     * 生成缴费流水号
     * 格式: PrOptionsHeat.serialPrefix + 日期(yyyyMMdd) + 自增序号
     *
     * @return 生成的流水号
     */
    String generateSerialNum();

    /**
     * 生成支付二维码URL
     *
     * @param type      支付类型: wechat / alipay / wechatH5
     * @param serialNum 流水号
     * @return 二维码URL
     */
    String generateQrCode(String type, String serialNum);

    /**
     * 检查支付是否成功
     * 查询 PrTransactionRecord 是否存在 status=0（正常）的记录
     *
     * @param serialNum 流水号
     * @return true=已支付
     */
    boolean checkPaymentStatus(String serialNum);

    /**
     * 根据流水号查询交易记录
     *
     * @param serialNum 流水号
     * @return 交易记录VO
     */
    PrTransactionRecordVo getRecordBySerialNum(String serialNum);
}
