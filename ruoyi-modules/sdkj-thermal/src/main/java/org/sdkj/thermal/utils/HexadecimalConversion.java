package org.sdkj.thermal.utils;

/**
 * 十六进制转换工具
 */
public class HexadecimalConversion {

    /**
     * 取余操作 16进制
     */
    public static String conversion16(String arg) {
        int b = 0;
        for (int i = 0; i < arg.length(); i++) {
            if (i % 2 == 0) {
                b += Integer.parseInt(arg.substring(i, i + 2), 16);
            }
        }
        if (b <= 255) {
            return String.format("%02X", b);
        } else {
            return String.format("%02X", b % 256);
        }
    }
}
