package com.ruoyi.connDev.modbus;

import java.net.Socket;
import java.util.Base64;
import java.util.List;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.constant.MbTranType;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.modbus.ByteConverter;
import com.ruoyi.common.utils.modbus.MRuleTools;
import com.ruoyi.common.utils.modbus.MTools;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.common.utils.spring.SpringUtils;

public class RedisReadTask implements Runnable {

    private Socket socket = null;
    private List<RegLib> regLibs = null;

    private static final String KEY_PREFIX = "mb:";

    public RedisReadTask(Socket socket, List<RegLib> regLibs) {
        this.socket = socket;
        this.regLibs = regLibs;
    }

    @Override
    public void run() {
        RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
        try {
            // 使用modbusUtils一次性读取1000个寄存器的数据
            byte[] data = new byte[2000];
            data = ModbusUtils.ReadHoldingRegister(socket, 0, 1000);
            // 把data进行解码然后存入redis中
            for (RegLib regLib : regLibs) {
                // data解码-首先按照RegAddr和regNum截取data
                byte[] src = subByteArray(data, regLib.getRegAddr() * 2, regLib.getRegNum() * 2);
                // skip rules
                if (!isValid(regLib)) {
                    continue;
                }
                // 定义前缀
                String key = KEY_PREFIX + regLib.getParamKey();
                // 获取精度
                int prec = MRuleTools.getPrec(regLib.getRegAddr(), getSysDotNum(data), getPrmWorP(data));

                // 解码
                byte[] res = MTools.decode(src, regLib.getRegNum(), regLib.getDataType());

                // 根据数据类型存储不同的值到redis
                switch (regLib.getDataType()) {
                    case MbTranType.U16:
                    case MbTranType.S16:
                        short shortVal = ByteConverter.bytesToShort(res);
                        Object shortResult = processValueWithPrecision(shortVal, prec);
                        redisCache.setCacheObject(key, shortResult);
                        // if (shortVal != 0) {
                        // System.out.println(regLib.getParamKey() + ":" + shortResult);
                        // }
                        break;
                    case MbTranType.U32:
                    case MbTranType.S32:
                        int intVal = ByteConverter.bytesToInt(res);
                        Object intResult = processValueWithPrecision(intVal, prec);
                        redisCache.setCacheObject(key, intResult);
                        // if (intVal != 0) {
                        // System.out.println(regLib.getParamKey() + ":" + intResult);
                        // }
                        break;
                    case MbTranType.U64:
                    case MbTranType.S64:
                        long longVal = ByteConverter.bytesToLong(res);
                        Object longResult = processValueWithPrecision(longVal, prec);
                        redisCache.setCacheObject(key, longResult);
                        // if (longVal != 0) {
                        // System.out.println(regLib.getParamKey() + ":" + longResult);
                        // }
                        break;
                    case MbTranType.F32:
                        Float floatVal = ByteConverter.bytesToFloat(res);
                        Object floatResult = processValueWithPrecision(floatVal, prec);
                        redisCache.setCacheObject(key, floatResult);
                        break;
                    case MbTranType.F64:
                        Double doubleVal = ByteConverter.bytesToDouble(res);
                        Object doubleResult = processValueWithPrecision(doubleVal, prec);
                        redisCache.setCacheObject(key, doubleResult);
                        break;
                    case MbTranType.Chars:
                        // System.out.println(regLib.getParamKey() + "::::::::::" + new String(res));
                        redisCache.setCacheObject(key, new String(res).trim());
                        break;
                    case MbTranType.Bytes:
                        // 转为base64存储
                        String base64 = Base64.getEncoder().encodeToString(res);
                        redisCache.setCacheObject(key, base64);
                        break;
                    default:
                        // 其余是list的情况
                        String type = checkStr(regLib.getDataType());
                        if (regLib.getParamKey().equals("doustatus")) {
                            System.out.println("斗状态" + ":" + new String(res));
                        }
                        redisCache.setCacheObject(key, ByteConverter.bytesToJsonArray(res, type));
                }

                // 处理regNum大于1的情况，这个时候通常是list
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object processValueWithPrecision(Number value, int prec) {
        if (prec == 0) {
            return value; // 返回原始整数值
        } else {
            if (value instanceof Short || value instanceof Integer || value instanceof Long) {
                return value.doubleValue() * Math.pow(10, -prec); // 返回经过处理的浮点数
            } else if (value instanceof Float || value instanceof Double) {
                return value.doubleValue() * Math.pow(10, -prec); // 返回经过处理的浮点数
            }
        }
        return null; // 或者可以返回其他默认值
    }

    // 获取sysDotNum
    private static int getSysDotNum(byte[] src) {
        byte[] res = subByteArray(src, 26, 1);
        res = MTools.decode(src, 1, MbTranType.U16);
        return ByteConverter.bytesToShort(res);
    }

    // 获取prmWordP
    private static int getPrmWorP(byte[] src) {
        byte[] res = subByteArray(src, 274, 1);
        res = MTools.decode(src, 1, MbTranType.U16);
        return ByteConverter.bytesToShort(res);
    }

    // 截取指定位置和长度的字节数组
    public static byte[] subByteArray(byte[] source, int startIndex, int length) {
        if (source == null || startIndex < 0 || length <= 0 || startIndex >= source.length) {
            return new byte[0]; // 返回空数组表示无效的参数或无法截取
        }

        int endIndex = Math.min(startIndex + length, source.length);
        int newLength = endIndex - startIndex;
        byte[] result = new byte[newLength];
        System.arraycopy(source, startIndex, result, 0, newLength);
        return result;
    }

    // 检查String中是否有'['字符，如果有，截取'['之前的字符，返回
    public static String checkStr(String str) {
        if (str.contains("[") && str.contains("]")) {
            return str.substring(0, str.indexOf("["));
        }
        return str;
    }

    public static void main(String[] args) {
        byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };
        int startIndex = 1; // 起始位置
        int length = 3; // 截取长度

        byte[] subArray = subByteArray(data, startIndex, length);

        // 打印截取后的字节数组
        for (byte b : subArray) {
            System.out.print(String.format("%02X ", b));
        }
    }

    // test：打印二进制数组的每一位
    public static void printBits(byte[] byteArray) {
        for (byte b : byteArray) {
            for (int i = 7; i >= 0; i--) {
                int bit = (b >> i) & 1;
                System.out.print(bit);
            }
            System.out.print(" "); // 在不同的字节之间添加空格，使输出更清晰
        }
    }

    // skip rules
    public static boolean isValid(RegLib regLib) {
        // 如果regLib.getRegAddr() > 999, 跳过
        if (regLib.getRegAddr() > 999) {
            return false;
        }
        // 如果regLib.getParamKey()是null或者_unused, 跳过
        if (regLib.getParamKey() == null || regLib.getParamKey().equals("_unused")) {
            return false;
        }
        return true;
    }

    // 处理list的情况
    // private void dealBytesByType(byte[] src, int len, String type, RedisCache
    // redisCache) {
    // List<HashMap<String, Object>> list = new ArrayList<>();
    // switch (type) {
    // case MbTranType.U8:
    // case MbTranType.S8:
    // for (int i = 0; i < len; i += 2) {
    // list.add((byte) src[i]);
    // list.append((quint8) src[i]);
    // }
    // case MbTranType.U16:
    // case MbTranType.S16:
    // case MbTranType.U32:
    // case MbTranType.S32:
    // case MbTranType.U64:
    // case MbTranType.S64:
    // case MbTranType.F32:
    // case MbTranType.F64:
    // default:
    // }
    // }
}
