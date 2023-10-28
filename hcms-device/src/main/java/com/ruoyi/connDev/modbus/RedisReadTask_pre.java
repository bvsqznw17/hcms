package com.ruoyi.connDev.modbus;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.constant.MbTranType;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.modbus.MRuleTools;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.common.utils.spring.SpringUtils;

public class RedisReadTask_pre implements Runnable {

    private Socket socket = null;
    private List<RegLib> regLibs = null;

    private static final String KEY_PREFIX = "mb:";

    public RedisReadTask_pre(Socket socket, List<RegLib> regLibs) {
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
            // 全部转为大端序
            ByteBuffer src = ByteBuffer.wrap(data);
            src.order(ByteOrder.LITTLE_ENDIAN);
            // 把data进行解码然后存入redis中
            for (RegLib regLib : regLibs) {
                // 如果regLib.getRegAddr() > 999, 跳过
                if (regLib.getRegAddr() > 999) {
                    continue;
                }
                // 定义前缀
                String key = KEY_PREFIX + regLib.getParamKey();
                // 移动到指定位置
                src.position(regLib.getRegAddr() * 2);
                // 获取精度
                int prec = MRuleTools.getPrec(regLib.getRegAddr(), getSysDotNum(src), getPrmWorP(src));
                // 处理regNum大于1的情况，这个时候通常是list
                // if (regLib.getRegNum() > 1) {
                // byte[] t = new byte[regLib.getRegNum() * 2];
                // t = src.get(t).array();
                // }
                switch (regLib.getDataType()) {
                    case MbTranType.U16:
                    case MbTranType.S16:
                        System.out.println(regLib.getParamKey() + ":" + src.getShort() * Math.pow(10, -prec));
                        redisCache.setCacheObject(key, src.getShort() * Math.pow(10, -prec));
                        break;
                    case MbTranType.U32:
                    case MbTranType.S32:
                        System.out.println(regLib.getParamKey() + ":" + src.getInt());
                        redisCache.setCacheObject(key, src.getInt() * Math.pow(10, -prec));
                        break;
                    case MbTranType.U64:
                    case MbTranType.S64:
                        System.out.println(regLib.getParamKey() + ":" + src.getLong());
                        redisCache.setCacheObject(key, src.getLong());
                        break;
                    case MbTranType.F32:
                        System.out.println(regLib.getParamKey() + ":" + src.getFloat());
                        redisCache.setCacheObject(key, src.getFloat());
                    case MbTranType.F64:
                        System.out.println(regLib.getParamKey() + ":" + src.getDouble());
                        redisCache.setCacheObject(key, src.getDouble());
                        break;
                    case MbTranType.Chars:
                    case MbTranType.Bytes:
                        byte[] tmp = new byte[regLib.getRegNum() * 2];
                        redisCache.setCacheObject(key, src.get(tmp).array());
                        System.out.println(regLib.getParamKey() + ":" + src.get(tmp).array().toString());
                        break;
                    default:
                        byte[] t = new byte[regLib.getRegNum() * 2];
                        redisCache.setCacheObject(key, src.get(t).array());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    // 获取sysDotNum
    private static int getSysDotNum(ByteBuffer src) {
        src.position(26 * 2);
        return src.getShort();
    }

    // 获取prmWordP
    private static int getPrmWorP(ByteBuffer src) {
        src.position(274 * 2);
        return src.getShort();
    }
}
