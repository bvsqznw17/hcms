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
                // 如果regLib.getRegAddr() > 999, 跳过
                if (regLib.getRegAddr() > 999) {
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
                        redisCache.setCacheObject(key, ByteConverter.bytesToShort(res));
                        break;
                    case MbTranType.U32:
                    case MbTranType.S32:
                        redisCache.setCacheObject(key, ByteConverter.bytesToInt(res));
                        break;
                    case MbTranType.U64:
                    case MbTranType.S64:
                        redisCache.setCacheObject(key, ByteConverter.bytesToLong(res));
                        break;
                    case MbTranType.F32:
                        redisCache.setCacheObject(key, ByteConverter.bytesToFloat(res));
                    case MbTranType.F64:
                        redisCache.setCacheObject(key, ByteConverter.bytesToDouble(res));
                        break;
                    case MbTranType.Chars:
                        redisCache.setCacheObject(key, new String(res));
                    case MbTranType.Bytes:
                        // 转为base64存储
                        String base64 = Base64.getEncoder().encodeToString(res);
                        redisCache.setCacheObject(key, base64);
                        break;
                    default:
                        // 其余是list的情况
                        redisCache.setCacheObject(key, ByteConverter.bytesToJsonArray(res, regLib.getDataType()));
                }

                // 处理regNum大于1的情况，这个时候通常是list
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
