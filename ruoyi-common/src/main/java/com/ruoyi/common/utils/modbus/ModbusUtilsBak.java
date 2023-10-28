package com.ruoyi.common.utils.modbus;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.FunctionCode;
import com.digitalpetri.modbus.codec.Modbus;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest;
import com.digitalpetri.modbus.responses.ModbusResponse;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

/***
 * modbus TCP协议Java通讯读取例子
 *
 *
 */
public class ModbusUtilsBak {

    Logger log = LoggerFactory.getLogger(ModbusUtilsBak.class);

    /**
     * tcp连接对象
     */
    private static ModbusTcpMaster master;
    /**
     * modbus ip地址
     */
    private static final String IP = "192.168.19.130";
    /**
     * 端口
     */
    private static final Integer PORT = 502;
    /**
     * modubs从站ID
     */
    private static final Integer UNIT_ID = 1;
    /**
     * 成功代码
     */
    private static final String SUCCESS_CODE = "0x000000";
    /**
     * 与modubs连接异常
     */
    private static final String COON_FAIL_CODE = "0x000001";
    /**
     * 向modubs发送命令执行异常
     */
    private static final String EXEC_FAIL_CODE = "0x000002";
    /**
     * 数据写入失败
     */
    private static final String WRITE_FAIL_CODE = "0x000004";

    /**
     * 数据类型：U/S表示unsigned和signed，A表示数组，一般U都是整数，S是浮点数
     */
    private static final String U8A = "U8A"; // 8位字节数组
    private static final String U16 = "U16";
    private static final String U16A = "U16A"; // 16位整型数组
    private static final String U32A = "U32A"; // 32位整型数组
    private static final String S32 = "S32";
    private static final String S32A = "S32A"; // 32位无符号整型数组

    /**
     * 获取TCP协议的Master
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void initModbusTcpMaster(String ip, int port) {
        if (master == null) {
            // 创建配置
            ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(ip).setPort(port).build();
            master = new ModbusTcpMaster(config);
        }
    }

    /***
     * 释放资源
     */
    public static void release() {
        if (master != null) {
            master.disconnect();
        }
        Modbus.releaseSharedResources();
    }

    /**
     * 读取HoldingRegister数据
     *
     * @param address  寄存器地址
     * @param quantity 寄存器数量
     * @param unitId   id
     * @return 读取结果
     * @throws InterruptedException 异常
     * @throws ExecutionException   异常
     */
    public static Number readHoldingRegisters(int address, int quantity, int unitId, String dataType)
            throws InterruptedException, ExecutionException {
        Number result = null;
        CompletableFuture<ReadHoldingRegistersResponse> future = master
                .sendRequest(new ReadHoldingRegistersRequest(address, quantity), unitId);
        ReadHoldingRegistersResponse readHoldingRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
        if (readHoldingRegistersResponse != null) {
            ByteBuf buf = readHoldingRegistersResponse.getRegisters();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            if (dataType.equals(U16)) { // U16
                result = buf.readShort();
            } else if (dataType.equals(S32)) { // S32
                result = TranStrTools.dealS32ByteData(bytes);
            }

            ReferenceCountUtil.release(readHoldingRegistersResponse);
        }
        return result;
    }

    public static String readHoldingRegisters(int address, int quantity, int unitId, String dataType, int arrLength)
            throws InterruptedException, ExecutionException {
        String result = "";
        CompletableFuture<ReadHoldingRegistersResponse> future = master
                .sendRequest(new ReadHoldingRegistersRequest(address, quantity), unitId);
        ReadHoldingRegistersResponse readHoldingRegistersResponse = future.get();// 工具类做的同步返回.实际使用推荐结合业务进行异步处理
        if (readHoldingRegistersResponse != null) {
            ByteBuf buf = readHoldingRegistersResponse.getRegisters();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);

            if (dataType.equals(U16)) { // U16
                result = buf.readShort() + "";
            } else if (dataType.equals(S32)) { // S32
                result = TranStrTools.dealS32ByteData(bytes) + "";
            } else if (dataType.equals(U8A)) { // U8A
                TranStrTools.swapBytesValue01(bytes);
                result = new String(bytes, StandardCharsets.UTF_8);
            }else if (dataType.equals(U16A)) {  // U16A
                System.out.println("deal u16a");
            }else if (dataType.equals(U32A)) {  // U32A
                System.out.println("deal u32a");
            }else if (dataType.equals(S32A)) {  // S32A
                result = TranStrTools.dealS32ByteDataArr(bytes);
            }
            
            ReferenceCountUtil.release(readHoldingRegistersResponse);
        }
        return result;
    }

    /**
     * @param address  寄存器地址
     * @param quantity 写位数
     * @param values   写入值
     * @description: 写HoldingRegister数据
     * @return: 结果值
     */
    public static String WriteMultipleRegisters(Integer address, Integer quantity, String dataType, String value, int arrLength) {
        try {
            byte[] values = new byte[arrLength];
            // 根据数据类型生成byte数组
            if (dataType.equals(U16)) { // U16
                values = TranStrTools.int2bytes2(Integer.parseInt(value));
            } else if (dataType.equals(S32)) { // S32
                values = TranStrTools.int2bytes4(Integer.parseInt(value));
            } else if (dataType.equals(U8A)) { // U8A
                values = TranStrTools.str2bytes(value, arrLength);
            }else if (dataType.equals(U16A)) {  // U16A
            }else if (dataType.equals(U32A)) {  // U32A
            }else if (dataType.equals(S32A)) {  // S32A
            }
            System.out.println(values);
            WriteMultipleRegistersRequest request = new WriteMultipleRegistersRequest(address, quantity, values);
            // 发送单个寄存器数据，一般是无符号16位值：比如10
            CompletableFuture<ModbusResponse> future = master.sendRequest(request, UNIT_ID);
            ModbusResponse modbusResponse;
            // 获取写入的响应流
            modbusResponse = future.get();
            if (modbusResponse == null) {
                System.out.println("FCSC-ExternalConnection WriteMultipleRegisters：modbusResponse is null ");
                return WRITE_FAIL_CODE;
            }
            // 获取写入的响应FunctionCode
            FunctionCode functionCode = modbusResponse.getFunctionCode();
            System.out.println(
                    "FCSC-ExternalConnection functionCode.getCode()===" + functionCode.getCode() + "=" + functionCode);
            if (functionCode == FunctionCode.WriteMultipleRegisters) {
                return SUCCESS_CODE;
            } else {
                return WRITE_FAIL_CODE;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return EXEC_FAIL_CODE;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return EXEC_FAIL_CODE;
        } finally {
            // String releaseRes = release();
            // //如果释放连接失败，返回执行失败
            // if (!SUCCESS_CODE.equals(releaseRes)) {
            // return releaseRes;
            // }
        }
    }

    /**
     * 将byteBuf转为str
     * 
     * @param buf
     * @return
     */
    public static String byteBuf2Str(ByteBuf buf) {
        String str = "";
        if (buf.hasArray()) {
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            // for (int i = 0; i < buf.readableBytes() - 1;i++){
            // // str = new String(bytes, i, buf.readableBytes());
            // str = new String(bytes, i, i+1);
            // System.out.println(str);
            // }
            System.out.println(new String(bytes, 0, buf.readableBytes()));
        }
        return str;
    }

    public static void main(String[] args) {
        try {
            // 初始化资源
            // IpUtils.telnet("192.168.1.111", 502, 3000);
            initModbusTcpMaster("192.168.1.111", 502);
            // initModbusTcpMaster("127.0.0.1", 502);

            // 执行操作

            // 读取模拟量
            // System.out.println(readHoldingRegisters(122, 1, 1, U16));
            // System.out.println(readHoldingRegisters(106, 10, 1, U8A, 3));

            // 写多个寄存器
            String s = "hello666";
            System.out.println(WriteMultipleRegisters(106, 10, U8A, s, 20));
            // 释放资源
            // release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
