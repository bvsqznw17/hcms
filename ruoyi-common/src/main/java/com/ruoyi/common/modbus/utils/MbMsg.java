package com.ruoyi.common.modbus.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.modbus.core.payloads.ModbusPayLoad;
import com.ruoyi.common.modbus.core.requests.ModbusRequest;
import com.ruoyi.common.modbus.core.responses.ReadCoilStatusResponse;
import com.ruoyi.common.modbus.core.responses.ReadHoldingRegisterResponse;
import com.ruoyi.common.modbus.core.responses.ReadInputRegisterResponse;
import com.ruoyi.common.modbus.core.responses.ReadInputStatusResponse;
import com.ruoyi.common.modbus.core.typed.ModbusFCode;
import com.ruoyi.common.utils.modbus.TranStrTools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MbMsg {

    /**
     * 数据类型：U/S表示unsigned和signed，A表示数组，一般U都是整数，S是浮点数
     */
    private static final String U8 = "U8";  // 8位无符号整型
    private static final String U8A = "U8A"; // 8位字节数组
    private static final String U16 = "U16";
    private static final String S16 = "S16";
    private static final String U16A = "U16A"; // 16位整型数组
    private static final String U32A = "U32A"; // 32位整型数组
    private static final String S32 = "S32";
    private static final String S32A = "S32A"; // 32位无符号整型数组
    private static final String MKVS = "MKVS"; // 模块版本和状态

    public static void encode(ModbusRequest request, ByteBuf buf) throws Exception {
        int sIndex = buf.writerIndex();
        buf.writeZero(7);
        int rIndex = buf.writerIndex();
        payload(request,buf);
        header(sIndex,rIndex, request.getFlag(),request.getPool(),request.getUid(),buf);
    }

    public static void decode(ByteBuf byteBuf, List<Object> list) throws Exception {
        int bytesLen = byteBuf.readableBytes();
        if (bytesLen < 11){
            byteBuf.skipBytes(bytesLen);
            return;
        }
        byteBuf.markReaderIndex();
        int flag    = byteBuf.readUnsignedShort();
        int pool    = byteBuf.readUnsignedShort();
        int dtLen   = byteBuf.readUnsignedShort();
        int uid     = byteBuf.readUnsignedByte();
        if(pool != 0){
            byteBuf.skipBytes(bytesLen);
            return;
        }
        int code    = byteBuf.readByte();
        int btLen   = byteBuf.readUnsignedByte();
        byte[] data = new byte[btLen];
        int index = byteBuf.readerIndex();
        byteBuf.getBytes(index,data,0,btLen);
        if(code == ModbusFCode.READ_COIL_STATUS){
            list.add(new ReadCoilStatusResponse(flag,uid,code,btLen,data));
        }
        if(code == ModbusFCode.READ_INPUT_STATUS){
            list.add(new ReadInputStatusResponse(flag,uid,code,btLen,data));
        }
        if(code == ModbusFCode.READ_HOLDING_REGISTER){
            list.add(new ReadHoldingRegisterResponse(flag,uid,code,btLen,data));
        }
        if(code == ModbusFCode.READ_INPUT_REGISTER){
            list.add(new ReadInputRegisterResponse(flag,uid,code,btLen,data));
        }
        // 无写入返回

        byteBuf.resetReaderIndex();
        byteBuf.readBytes(bytesLen);
    }

    public static String readDataFromRes(ByteBuf byteBuf, String dataType) throws Exception {
        String result = "";
        int bytesLen = byteBuf.readableBytes();
        if (bytesLen < 11){
            byteBuf.skipBytes(bytesLen);
            return null;
        }
        byteBuf.markReaderIndex();
        int flag    = byteBuf.readUnsignedShort();
        int pool    = byteBuf.readUnsignedShort();
        int dtLen   = byteBuf.readUnsignedShort();
        int uid     = byteBuf.readUnsignedByte();
        if(pool != 0){
            byteBuf.skipBytes(bytesLen);
            return null;
        }
        int code    = byteBuf.readByte();
        int btLen   = byteBuf.readUnsignedByte();
        byte[] data = new byte[btLen];
        int index = byteBuf.readerIndex();
        byteBuf.getBytes(index,data,0,btLen);

        // 根据dataType返回不同的result
        if (dataType.equals(U16) || dataType.equals(U8)) { // U16
            result = byteBuf.readShort() + "";
        } else if (dataType.equals(S16)) { // S16
            result = byteBuf.readShort() + "";
        } else if (dataType.equals(S32)) { // S32
            result = TranStrTools.dealS32ByteData(data) + "";
        } else if (dataType.equals(U8A)) { // U8A
            TranStrTools.swapBytesValue01(data);
            result = new String(data, StandardCharsets.UTF_8);
        }else if (dataType.equals(U16A)) {  // U16A
            System.out.println(btLen);
            result = TranStrTools.tranU16AData(byteBuf, btLen);
        }else if (dataType.equals(U32A)) {  // U32A
            System.out.println("deal u32a");
        }else if (dataType.equals(S32A)) {  // S32A
            result = TranStrTools.dealS32ByteDataArr(data);
        }else if (dataType.equals(MKVS)) {  // MKVS
            result = TranStrTools.dealMKVSByteData(data);
        }
        return result.trim();
        // 无写入返回

        // byteBuf.resetReaderIndex();
        // byteBuf.readBytes(bytesLen);
    }

    public static void header(int sIndex ,int rIndex,int flag,short pool, short uid,ByteBuf buf){
        int layLen = buf.writerIndex() - rIndex;
        int nIndex = buf.writerIndex();
        buf.writerIndex(sIndex);
        buf.writeShort(flag);
        buf.writeShort(pool);
        buf.writeShort(layLen + 1);
        buf.writeByte(uid);
        buf.writerIndex(nIndex);
    }

    public static void payload(ModbusRequest request,ByteBuf buf){
        int code = request.getPayLoad().getCode();
        buf.writeByte(code);
        switch (code){
            case ModbusFCode.READ_COIL_STATUS:
            case ModbusFCode.READ_INPUT_STATUS:
            case ModbusFCode.READ_HOLDING_REGISTER:
            case ModbusFCode.READ_INPUT_REGISTER:
                currency(request.getPayLoad(),buf);
                break;
            case ModbusFCode.WRITE_SINGLE_COIL:
                writeSingleCoil(request.getPayLoad(),buf);
                break;
            case ModbusFCode.WRITE_SINGLE_REGISTER:
                writeSingleRegister(request.getPayLoad(),buf);
                break;
            case ModbusFCode.WRITE_MULTIPLE_COIL:
                writeMultipleCoil(request.getPayLoad(),buf);
                break;
            case ModbusFCode.WRITE_MULTIPLE_REGISTER:
                writeMultipleRegister(request.getPayLoad(),buf);
                break;
            default:
                throw new RuntimeException(String.format("%s is an unsupported method",code));
        }
    }

    public static void currency(ModbusPayLoad payload, ByteBuf buf){
        buf.writeShort(payload.getAddress());
        buf.writeShort(payload.getAmount());
    }

    public static void writeSingleCoil(ModbusPayLoad<Integer> payload,ByteBuf buf){
        buf.writeShort(payload.getAddress());
        buf.writeShortLE(payload.val());
      //  buf.writeByte(payload.value());
    }
    public static void writeSingleRegister(ModbusPayLoad<Short> payload,ByteBuf buf){
        buf.writeShort(payload.getAddress());
        buf.writeShort(payload.val());
    }

    public static void writeMultipleCoil(ModbusPayLoad<Integer> payload,ByteBuf buf){
        currency(payload,buf);
        int count = (payload.getAmount() + 7) / 8;
        buf.writeByte(count);
        ByteBuf temp = Unpooled.buffer();
        temp.writeShortLE(payload.val());
        buf.writeBytes(temp,count);
    }

    public static void writeMultipleRegister(ModbusPayLoad<byte[]> payload,ByteBuf buf){
        currency(payload,buf);
        int count = payload.getAmount() * 2;
        buf.writeByte(count);
        ByteBuf temp = Unpooled.buffer();
        temp.writeBytes(payload.val());
        System.out.println(Arrays.toString(payload.val()));
        buf.writeBytes(temp,count);
    }


}
