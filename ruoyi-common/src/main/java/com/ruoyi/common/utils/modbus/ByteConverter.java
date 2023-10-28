package com.ruoyi.common.utils.modbus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.constant.MbTranType;

public class ByteConverter {
    // 将byte[]转换为short
    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    // 将byte[]转换为int
    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    // 将byte[]转换为long
    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    // 将byte[]转换为float
    public static float bytesToFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }

    // 将byte[]转换为double
    public static double bytesToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

    // 将byte[]数组转换为JSON数组字符串
    public static String bytesToJsonArray(byte[] bytes, String dataType) {
        JSONArray jsonArray = new JSONArray();
        int elementTypeSize = getTypeSize(dataType);
        // int numElements = bytes.length / elementTypeSize;

        for (int i = 0; i < bytes.length; i += elementTypeSize) {
            byte[] elementBytes = Arrays.copyOfRange(bytes, i, i + elementTypeSize);
            jsonArray.add(convertBytesToType(elementBytes, dataType));
        }

        return jsonArray.toJSONString();
    }

    // 获取数据类型的字节大小
    private static int getTypeSize(String dataType) {
        switch (dataType) {
            case MbTranType.U8:
            case MbTranType.S8:
            case MbTranType.U8A:
                return 1;
            case MbTranType.U16:
            case MbTranType.S16:
            case MbTranType.U16A:
            case MbTranType.S16A:
                return 2;
            case MbTranType.U32:
            case MbTranType.S32:
            case MbTranType.F32:
            case MbTranType.U32A:
            case MbTranType.S32A:
                return 4;
            case MbTranType.U64:
            case MbTranType.S64:
            case MbTranType.F64:
                return 8;
            default:
                // System.out.println("Unsupported data type: " + dataType);
                return 1;
            // throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }

    // 根据数据类型将byte[]转换为相应类型
    private static Object convertBytesToType(byte[] bytes, String dataType) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        switch (dataType) {
            case MbTranType.U8:
                return buffer.get() & 0xFF;
            case MbTranType.S8:
                return buffer.get();
            case MbTranType.U16:
                return buffer.getShort() & 0xFFFF;
            case MbTranType.S16:
                return buffer.getShort();
            case MbTranType.U32:
                return buffer.getInt() & 0xFFFFFFFFL;
            case MbTranType.S32:
                return buffer.getInt();
            case MbTranType.F32:
                return buffer.getFloat();
            case MbTranType.U64:
                return buffer.getLong();
            case MbTranType.S64:
                return buffer.getLong();
            case MbTranType.F64:
                return buffer.getDouble();
            default:
                // System.out.println("Unsupported data type: " + dataType);
                return "-99999999";
            // throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
    }

    public static void main(String[] args) {
        // 示例用法
        byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 }; // 4个字节的byte数组
        short shortValue = bytesToShort(data);
        int intValue = bytesToInt(data);
        long longValue = bytesToLong(data);
        float floatValue = bytesToFloat(data);
        double doubleValue = bytesToDouble(data);

        System.out.println("Short: " + shortValue);
        System.out.println("Int: " + intValue);
        System.out.println("Long: " + longValue);
        System.out.println("Float: " + floatValue);
        System.out.println("Double: " + doubleValue);

        String jsonArrayString = bytesToJsonArray(data, MbTranType.U16); // 每个元素4字节，1个元素
        System.out.println("JSON Array: " + jsonArrayString);
    }
}
