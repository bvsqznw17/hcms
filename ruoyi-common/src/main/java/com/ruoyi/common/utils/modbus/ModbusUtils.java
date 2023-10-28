package com.ruoyi.common.utils.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.ruoyi.common.modbus.core.payloads.ReadHoldingRegisterPayLoad;
import com.ruoyi.common.modbus.core.payloads.WriteMultipleRegisterPayLoad;
import com.ruoyi.common.modbus.core.requests.ModBusTcpRequest;
import com.ruoyi.common.modbus.core.requests.ModbusRequest;
import com.ruoyi.common.modbus.core.value.MultipleValue;
import com.ruoyi.common.modbus.utils.MbMsg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * modbusTCP通信工具类
 */
public class ModbusUtils {

    // modbus从站Id
    private static final Integer UNIT_ID = 1;
    // modbus命令字地址
    private static final int CMD_ADDR = 816;
    // modbus命令参数地址
    private static final int CMD_PARAM_ADDR = 815;

    /**
     * 数据类型：U/S表示unsigned和signed，A表示数组，一般U都是整数，S是浮点数
     */
    private static final String U8 = "U8"; // 8位无符号整型
    private static final String U8A = "U8A"; // 8位字节数组
    private static final String U16 = "U16";
    private static final String S16 = "S16";
    private static final String U16A = "U16A"; // 16位整型数组
    private static final String S16A = "S16A"; // 16位整型数组
    private static final String U32A = "U32A"; // 32位整型数组
    private static final String S32 = "S32";
    private static final String S32A = "S32A"; // 32位无符号整型数组

    /**
     * 写HoldingRegister数据
     * 
     * @throws Exception
     */
    public static String ReadHoldingRegister(Socket sct, int addr, int regNum, String dataType) throws Exception {
        String res = "";
        InputStream in = sct.getInputStream();
        OutputStream out = sct.getOutputStream();
        ModbusRequest req = new ModBusTcpRequest((short) 1,
                new ReadHoldingRegisterPayLoad(addr, regNum));
        ByteBuf bf = Unpooled.buffer(12);
        MbMsg.encode(req, bf);
        byte[] bytes = bf.array();
        out.write(bytes);
        out.flush();
        // 接收
        bf = Unpooled.buffer(2048);

        int dataLength = bf.readableBytes(); // 获取实际数据的长度
        bf.writeBytes(in, dataLength);
        res = MbMsg.readDataFromRes(bf, TranStrTools.transDataType(dataType));
        // System.out.println("客户端：" + res);
        return res;
    }

    /**
     * 写HoldingRegister数据
     * 返回byte数组
     */
    public static byte[] ReadHoldingRegister(Socket sct, int addr, int regNum) throws Exception {
        InputStream in = sct.getInputStream();
        OutputStream out = sct.getOutputStream();
        ModbusRequest req = new ModBusTcpRequest((short) 1,
                new ReadHoldingRegisterPayLoad(addr, regNum));
        ByteBuf bf = Unpooled.buffer(12);
        MbMsg.encode(req, bf);
        byte[] bytes = bf.array();
        out.write(bytes);
        out.flush();
        // 接收
        int len = regNum * 2 + 9;
        bf = Unpooled.buffer(len);
        bf.writeBytes(in, len);
        byte[] res = bf.array();
        System.out.println("客户端：" + res);
        // 去掉前面的9个字节
        res = Arrays.copyOfRange(res, 9, res.length);
        return res;
    }

    /**
     * 写HoldingRegister数据
     * 返回解码后byte数组
     */
    public static byte[] ReadHoldingRegister2(Socket sct, int addr, int regNum) throws Exception {
        InputStream in = sct.getInputStream();
        OutputStream out = sct.getOutputStream();
        ModbusRequest req = new ModBusTcpRequest((short) 1,
                new ReadHoldingRegisterPayLoad(addr, regNum));
        ByteBuf bf = Unpooled.buffer(12);
        MbMsg.encode(req, bf);
        byte[] bytes = bf.array();
        out.write(bytes);
        out.flush();
        // 接收
        int len = regNum * 2 + 9;
        bf = Unpooled.buffer(len);
        bf.writeBytes(in, len);
        byte[] res = bf.array();
        System.out.println("客户端：" + res);
        // 去掉前面的9个字节
        res = Arrays.copyOfRange(res, 9, res.length);
        // 基于MODBUSRTU规则进行解码
        return res;
    }

    /**
     * @param address  寄存器地址
     * @param quantity 写位数
     * @param values   写入值
     * @description: 写HoldingRegister数据
     * @return: 结果值
     */
    public static boolean WriteMultipleRegisters(Socket sct, Integer address, Integer quantity, String dataType,
            String... value) {
        try {
            String v1 = value[0];
            System.out.println(v1);
            byte[] values = new byte[quantity * 2];
            dataType = TranStrTools.transDataType(dataType);
            // 根据数据类型生成byte数组
            if (dataType.equals(U16) || dataType.equals(U8)) { // U16
                values = TranStrTools.int2bytes2(Integer.parseInt(v1));
            } else if (dataType.equals(S32)) { // S32
                values = TranStrTools.int2bytes4(Integer.parseInt(v1));
            } else if (dataType.equals(U8A)) { // U8A
                values = TranStrTools.str2bytes(v1, quantity * 2);
            } else if (dataType.equals(U16A)) { // U16A
                values = TranStrTools.U16AStr2Byte(value);
            } else if (dataType.equals(U32A)) { // U32A
                // values = TranStrTools.S32AStr2Byte(value);
            } else if (dataType.equals(S32A)) { // S32A
                values = TranStrTools.S32AStr2Byte(value);
            }

            // 获取指定devName标识的线程的socket
            OutputStream out = sct.getOutputStream();
            InputStream in = sct.getInputStream();
            ModbusRequest req = new ModBusTcpRequest(
                    new WriteMultipleRegisterPayLoad(address, new MultipleValue(values, quantity)));
            ByteBuf bf = Unpooled.buffer(quantity * 2 + 13);
            MbMsg.encode(req, bf);
            byte[] bytes = bf.array();
            System.out.println(Arrays.toString(bytes));
            out.write(bytes);
            out.flush();
            // 获取返回信号
            bf = Unpooled.buffer();
            bf.writeBytes(in, 128);
            System.out.println(Arrays.toString(bf.array()));
            // String res = MbMsg.readDataFromRes(bf, "U8A");
            String res = MbMsg.readDataFromRes(bf, "U16");
            // String res = MbMsg.readDataFromRes(bf, "S16");
            System.out.println("客户端：" + res);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 向modbus设备发送指令
     * 
     */
    public static void writeCmd(Socket sct, String... value) {
        // 向modbus设备发送指令
        try {
            WriteMultipleRegisters(sct, CMD_ADDR, 1, U16, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下发modbus命令和参数
     */
    public static void writeCmdParam(Socket sct, String cmd, String cmdParam) {
        // 向modbus设备发送命令和参数
        try {
            // 把命令和参数拼接起来作为指令：cmd 2个字节 + cmdParam 2个字节
            // 转为short再转为字节拼接之后再转为整数
            short cmdShort = Short.parseShort(cmd);
            short cmdParamShort = Short.parseShort(cmdParam);

            // 将 cmd 和 cmdParam 合并成一个 4 字节的整数
            int value = ((int) cmdShort << 16) | (cmdParamShort & 0xFFFF);

            WriteMultipleRegisters(sct, CMD_PARAM_ADDR, 2, S32, value + "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取modbus设备发送指令后的数据
     * 
     */
    public static String readCmdResult(Socket sct) throws Exception {
        return ReadHoldingRegister(sct, CMD_PARAM_ADDR, 1, U16);
    }

    /**
     * 获取UUID
     * 
     * @throws IOException
     */
    public static String getUUID(OutputStream os, InputStream in) {

        String uuid = "";
        ByteBuf buf = Unpooled.buffer(7);
        int sIndex = buf.writerIndex();
        buf.writeZero(7);
        int rIndex = buf.writerIndex();
        buf.writerIndex(sIndex);
        int flag = 0x7fff;
        int pool = 0x0003;
        int func = 0x0001;
        int end = 0x00;
        buf.writeShort(flag);
        buf.writeShort(pool);
        buf.writeShort(func);
        buf.writeByte(end);
        buf.writerIndex(rIndex);
        // 发送请求
        try {
            byte[] bytes = buf.array();
            System.out.println(Arrays.toString(bytes));
            os.write(bytes);
            os.flush();
            // 读取请求
            buf = Unpooled.buffer(64);
            buf.writeBytes(in, 64);
            uuid = cutUUID(buf.array());
        } catch (Exception e) {
            System.out.println("获取UUID失败");
            e.printStackTrace();
        }
        System.out.println("uuid：" + uuid);
        return uuid;
    }

    /**
     * 截取uuid
     */
    public static String cutUUID(byte[] bts) {
        byte[] btmp = new byte[38];
        System.arraycopy(bts, 7, btmp, 0, 38);
        String uuid = new String(btmp, StandardCharsets.UTF_8);
        return uuid;
    }

    public static void main(String[] args) {
        int[] mainArr = { 0x10, 0x20, 0x30, 0x40 };
        // subModel 0-4，在组装的时候添加在选定的mainModel后面，比如0x10，0x11，0x12，0x13
        int[] subArr = { 0x00, 0x01, 0x02, 0x03, 0x04 };
        // 命令参数
        int cmdParam = mainArr[0] + subArr[2];
        System.out.println(cmdParam + "");

    }

    /**
     * 基于MODBUSRTU规则进行解码
     */
    // private static byte[] decodeModbus(byte[] bytes) {
    // ByteBuffer buffer = ByteBuffer.allocate(modbusRtuFrame.length * 2);
    // buffer.order(ByteOrder.LITTLE_ENDIAN);

    // for (int u16Value : modbusRtuFrame) {
    // buffer.putShort((short) u16Value);
    // }

    // return buffer.array();
    // return bytes;
    // }

}
