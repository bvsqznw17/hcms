package com.ruoyi.common.utils.modbus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.ruoyi.common.modbus.core.business.MKVS;

import io.netty.buffer.ByteBuf;

/**
 * 存放用于处理modbus接收的字符串的辅助函数
 */
public class TranStrTools {

    /**
     * 将bytes字符数组奇偶位进行置换
     * 用于交换字节序，将奇偶位进行互换
     */
    public static void swapBytesValue01(byte[] bytes) {
        for (int i = 0; i < bytes.length; i += 2) {
            byte tmp = bytes[i];
            bytes[i] = bytes[i + 1];
            bytes[i + 1] = tmp;
        }
    }

    /**
     * A Java function that converts a ByteBuf to a String representation of a byte
     * array.
     *
     * @param bf  The ByteBuf to be converted.
     * @param len The length of the ByteBuf.
     * @return A String representation of the byte array.
     */
    public static String tranU16AData(ByteBuf bf, int len) {
        String res = "[";
        for (int i = 0; i < len; i += 2) {
            res += bf.readShort() + ",";
            System.out.println(res);
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }

    /*
     * 处理S32：将16位byte字符数组做整型数据处理并计算
     * 将16位的字节数组转换为有符号32位整数
     */
    public static int dealS32ByteData(byte[] bytes) {
        int res = 0;
        Integer a = bytes[0] & 0xff;
        Integer b = bytes[1] & 0xff;
        Integer c = bytes[3] & 0xff;
        res = c * 65536 + a * 256 + Integer.parseUnsignedInt(b.toString());
        return res;
    }

    /*
     * 处理S32数组：将bytes分解为每4个字节一段然后调用 dealS32ByteData 后组装为[1,2,3]形式的字符串返回
     * 处理S32数组，将字节数组分解为4字节一段，每段转换为有符号32位整数，并以字符串数组形式返回
     */
    public static String dealS32ByteDataArr(byte[] bytes) {
        String res = "[";
        for (int i = 0; i < bytes.length; i += 4) {
            byte[] tmp = { bytes[i], bytes[i + 1], bytes[i + 2], bytes[i + 3] };
            res += dealS32ByteData(tmp) + ",";
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }

    /**
     * 将byte数组转为模块状态
     * 将字节数组解析为模块状态信息
     */
    public static String dealMKVSByteData(byte[] data) {
        List<MKVS> res = new ArrayList<>();
        // 每隔6位分割成一个MKVS
        for (int i = 0; i < data.length; i += 6) {
            // 先取4位转为String
            byte[] tmp = { data[i], data[i + 1], data[i + 2], data[i + 3] };
            String version = new String(tmp, StandardCharsets.UTF_8);
            // 再取1位转为int，另一位丢弃
            int status = data[i + 5] & 0xff;
            MKVS mkvs = new MKVS();
            mkvs.setVersion(version);
            mkvs.setStatus(status);
            res.add(mkvs);
        }
        return res.toString();
    }

    /**
     * 将整数转为只有一个字节的byte数组
     * 将整数转换为只有一个字节的字节数组
     */
    public static byte[] int2bytes1(int n) {
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    /**
     * 整数转为二进制数组：大端转小端
     * 将整数转换为两个字节的字节数组，进行大端字节序到小端字节序的转换
     */
    public static byte[] int2bytes2(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n & 0xff);
        return b;
    }

    /**
     * S32转为二进制数组: 小端套大端
     * 将整数转换为四个字节的字节数组，进行小端字节序到大端字节序的转换
     */
    public static byte[] int2bytes4(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 16 & 0xff);
        return b;
    }

    /**
     * U8A转为二进制数组：字符串转二进制，遵循大端序，把每一个字节
     * 将字符串转换为字节数组，按大端字节序排列
     */
    public static byte[] str2bytes(String s, int len) {
        byte[] a = new byte[len];
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        // 将数组b的值错位赋值给数组a
        for (int i = 0; i < b.length; i += 2) {
            if (i + 1 < b.length) {
                a[i] = b[i + 1];
            }
            a[i + 1] = b[i];
        }
        return a;
    }

    /**
     * U16A转为二进制数组
     * 将U16A形式的字符串数组转换为字节数组
     */
    public static byte[] U16AStr2Byte(String[] strs) {
        byte[] res = new byte[strs.length * 2];
        for (String s : strs) {
            int num = Integer.parseInt(s);
            byte[] b = int2bytes2(num);
            res = arrayMerge(res, b);
        }
        return res;
    }

    /**
     * U32A转为二进制数组
     * 将U32A形式的字符串数组转换为字节数组
     */
    public static byte[] S32AStr2Byte(String[] strs) {
        byte[] res = new byte[strs.length * 2];
        for (String s : strs) {
            int num = Integer.parseInt(s);
            byte[] b = int2bytes4(num);
            res = arrayMerge(res, b);
        }
        return res;
    }

    /**
     * 将String转为short数组
     * 将字符串转换为short类型的数组
     */
    public static short[] str2shorts(String str) {
        char[] chars = str.toCharArray();
        short[] shorts = new short[str.length()];
        for (int i = 0; i < chars.length; i++) {
            shorts[i] = (short) chars[i];
        }
        return shorts;
    }

    /**
     * 对数据类型进行转换
     * 对数据类型字符串进行转换，例如将"U16[4]"转换为"U16A"
     */
    public static String transDataType(String dataType) {
        // 如果字符串中包含'[',将后面一部分都替换为A
        if (dataType != null && dataType.contains("[")) {
            dataType.substring(0, dataType.lastIndexOf("["));
            dataType += "A";
        }
        return dataType;
    }

    /**
     * 对数据类型进行转换
     * 获取数据类型字符串中包含的数组长度，如果没有则返回1
     */
    public static int getArrLen(String dataType) {
        // 如果字符串中包含'[',把中括号里的数字提取出来
        if (dataType != null && dataType.contains("[")) {
            String tmp = dataType.substring(dataType.lastIndexOf("[") + 1, dataType.lastIndexOf("]"));
            return Integer.parseInt(tmp);
        }
        return 1;
    }

    /**
     * 有符号字符串转整数
     * 将包含有符号标记的字符串转换为整数，例如"-123"转为-123
     */
    public static int strTransInt(String str) {
        if (str.contains("-")) {
            str = str.replace("-", "0");
            return -Integer.parseInt(str);
        }
        return Integer.parseInt(str);
    }

    /**
     * byte数组合并
     * 将两个字节数组合并为一个
     */
    public static byte[] arrayMerge(byte[] a, byte[] b) {
        byte[] res = new byte[a.length + b.length];
        System.arraycopy(a, 0, res, 0, a.length);
        System.arraycopy(b, 0, res, a.length, b.length);
        return res;
    }
}
