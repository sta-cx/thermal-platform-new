package org.sdkj.thermal.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 云谷/新奥第三方 API Token 工具类
 * 移植自旧系统 com.thermal.utils.YunGuUtils
 */
public class YunGuUtils {

    private YunGuUtils() {
    }

    /**
     * 生成云谷 API Token（MD5 摘要）
     *
     * @param appCode   分配的 AppCode
     * @param appSecret 分配的 AppSecret
     * @param timestamp 13位毫秒级时间戳字符串
     * @return 32位小写 MD5
     */
    public static String generateToken(String appCode, String appSecret, String timestamp) {
        if (isEmpty(appCode) || isEmpty(appSecret)) {
            throw new IllegalArgumentException("AppCode和AppSecret不能为空");
        }
        // 拼接：body|AppCode|AppSecret|Timestamp
        String rawStr = String.format("body|%s|%s|%s", appCode, appSecret, timestamp);
        return md5(rawStr);
    }

    /**
     * 新奥 Token 校验：异或还原出固定 key
     *
     * @param token     前端传来的 hex token
     * @param timeStamp 10位秒级时间戳
     * @return 还原后的 hex 字符串（与固定值比对）
     */
    public static String checkToken(String token, long timeStamp) {
        byte[] tokenBytes = hexStringToBytes(token);
        byte[] timeBytes = get16BytesFromLong(timeStamp);
        byte[] checkKey = new byte[16];
        for (int i = 0; i < 16; i++) {
            checkKey[i] = (byte) (tokenBytes[i] ^ timeBytes[i]);
        }
        return bytesToHexString(checkKey);
    }

    // ========== 内部工具方法 ==========

    private static String md5(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes("UTF-8"));
            BigInteger bigInt = new BigInteger(1, digest);
            String md5 = bigInt.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] get16BytesFromLong(long num) {
        byte[] bytes = new byte[16];
        for (int i = 15; i >= 8; i--) {
            bytes[i] = (byte) (num & 0xFF);
            if (bytes[i] == 0) {
                bytes[i] = (byte) (0xE5 ^ bytes[15]);
            }
            num >>>= 8;
        }
        for (int i = 0; i <= 7; i++) {
            bytes[i] = bytes[15 - i];
        }
        return bytes;
    }

    private static byte[] hexStringToBytes(String hexStr) {
        int len = hexStr.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return bytes;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
