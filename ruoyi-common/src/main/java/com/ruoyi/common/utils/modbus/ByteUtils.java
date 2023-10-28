package com.ruoyi.common.utils.modbus;

import java.util.Base64;

public class ByteUtils {

    // 把字节数组转为Base64
    public static String bytesToBase64(byte[] bytes) {
        String base64Encoded = Base64.getEncoder().encodeToString(bytes);
        return base64Encoded;
    }

    // 把Base64转为字节数组
    public static byte[] base64ToBytes(String base64Encoded) {
        return Base64.getDecoder().decode(base64Encoded);
    }

}
