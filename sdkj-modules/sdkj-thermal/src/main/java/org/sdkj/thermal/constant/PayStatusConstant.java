package org.sdkj.thermal.constant;

/**
 * 支付状态常量
 */
public class PayStatusConstant {

    // 订单状态
    public static final int ORDER_STATUS_PENDING = 0;     // 待支付
    public static final int ORDER_STATUS_SUCCESS = 1;     // 支付成功
    public static final int ORDER_STATUS_FAILED = 2;      // 支付失败
    public static final int ORDER_STATUS_CANCELLED = 3;   // 已取消
    public static final int ORDER_STATUS_REFUNDED = 4;    // 已退款

    // 退款状态
    public static final int REFUND_STATUS_PROCESSING = 0; // 申请中
    public static final int REFUND_STATUS_SUCCESS = 1;    // 退款成功
    public static final int REFUND_STATUS_FAILED = 2;     // 退款失败
}
