package com.ruoyi.common.utils.modbus;

import com.ruoyi.common.constant.MbTranType;

public class MTools {

    public static void setByte(byte[] data, int index, short v) {
        data[index] = (byte) (v >> 8);
        data[index + 1] = (byte) (v >> 0);
    }

    public static void setByte(byte[] data, int index, int v) {
        setByte(data, index + 2, (short) (v >> 16));
        setByte(data, index, (short) (v >> 0));
    }

    public static void setByte(byte[] data, int index, long v) {
        setByte(data, index + 6, (short) (v >> 48));
        setByte(data, index + 4, (short) (v >> 32));
        setByte(data, index + 2, (short) (v >> 16));
        setByte(data, index, (short) (v >> 0));
    }

    public static void setByte(byte[] data, int index, float v) {
        int i = Float.floatToIntBits(v);
        setByte(data, index, i);
    }

    public static void setByte(byte[] data, int index, double v) {
        long l = Double.doubleToLongBits(v);
        setByte(data, index, l);
    }

    public static void getByte2(byte[] src, byte[] dst) {
        short v = (short) ((src[0] << 8) & 0xFF00);
        v |= (short) (src[1] & 0xFF);
        dst[0] = (byte) ((v >> 8) & 0xFF);
        dst[1] = (byte) (v & 0xFF);
    }

    public static void getByte4(byte[] src, byte[] dst) {
        short v1 = getByte2(src, 0);
        short v2 = getByte2(src, 2);
        int v = ((v2 << 16) & 0xFFFF0000) | (v1 & 0xFFFF);
        dst[0] = (byte) ((v >> 24) & 0xFF);
        dst[1] = (byte) ((v >> 16) & 0xFF);
        dst[2] = (byte) ((v >> 8) & 0xFF);
        dst[3] = (byte) (v & 0xFF);
    }

    public static void getByte8(byte[] src, byte[] dst) {
        short v1 = getByte2(src, 0);
        short v2 = getByte2(src, 2);
        short v3 = getByte2(src, 4);
        short v4 = getByte2(src, 6);

        long v = ((long) v4 << 48) & 0xFFFF000000000000L;
        v |= ((long) v3 << 32) & 0xFFFF00000000L;
        v |= ((long) v2 << 16) & 0xFFFF0000L;
        v |= (long) v1 & 0xFFFFL;

        dst[0] = (byte) ((v >> 56) & 0xFF);
        dst[1] = (byte) ((v >> 48) & 0xFF);
        dst[2] = (byte) ((v >> 40) & 0xFF);
        dst[3] = (byte) ((v >> 32) & 0xFF);
        dst[4] = (byte) ((v >> 24) & 0xFF);
        dst[5] = (byte) ((v >> 16) & 0xFF);
        dst[6] = (byte) ((v >> 8) & 0xFF);
        dst[7] = (byte) (v & 0xFF);
    }

    public static short getByte2(byte[] src, int srcPos) {
        return (short) (((src[srcPos] & 0xFF) << 8) | (src[srcPos + 1] & 0xFF));
    }

    public static byte[] decode(byte[] src, int len, String dataType) {
        byte[] data = new byte[8];

        switch (dataType) {
            case MbTranType.U16:
            case MbTranType.S16:
                getByte2(src, data);
                break;
            case MbTranType.U32:
            case MbTranType.S32:
                getByte4(src, data);
                break;
            case MbTranType.U64:
            case MbTranType.S64:
                getByte8(src, data);
                break;
            case MbTranType.F32:
                getByte4(src, data);
            case MbTranType.F64:
                getByte8(src, data);
                break;
            case MbTranType.Chars:
            case MbTranType.Bytes:
                data = swapBytes(src, len);
                break;
            default:
                // data = swapBytes(src, len);
                return data;
        }
        return data;
    }

    private static byte[] swapBytes(byte[] src, int regNum) {
        byte[] newData = new byte[regNum];
        for (int i = 0; i < regNum; i += 2) {
            newData[i] = src[i + 1];
            newData[i + 1] = src[i];
        }
        return newData;
    }

}
