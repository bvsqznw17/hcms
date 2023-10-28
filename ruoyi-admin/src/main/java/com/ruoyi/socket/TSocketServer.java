package com.ruoyi.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import com.ruoyi.common.modbus.core.payloads.ReadHoldingRegisterPayLoad;
import com.ruoyi.common.modbus.core.requests.ModBusTcpRequest;
import com.ruoyi.common.modbus.core.requests.ModbusRequest;
import com.ruoyi.common.modbus.utils.MbMsg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public class TSocketServer {

        /**
     * 数据类型：U/S表示unsigned和signed，A表示数组，一般U都是整数，S是浮点数
     */
    private static final String U8A = "U8A"; // 8位字节数组
    private static final String U16 = "U16";
    private static final String S16 = "S16";
    private static final String U16A = "U16A"; // 16位整型数组
    private static final String S16A = "S16A"; // 16位整型数组
    private static final String U32A = "U32A"; // 32位整型数组
    private static final String S32 = "S32";
    private static final String S32A = "S32A"; // 32位无符号整型数组
    // TODO 接收devName和ip地址，存储到一个map中，然后在客户端读取这个map的ip地址来连接到本地服务器
    // 看看每次获取的ip是否是相同的即可，是否是真实可用的Ip地址和端口
    // 看看通过这ip地址和端口发起tcp连接是否能够获得响应
    public void startSocketServer(int port){
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("启动服务器....");
            Socket s = ss.accept();
            String ip = s.getInetAddress().getHostAddress();
            System.out.println("客户端:"+ip+"已连接到服务器");
            // 启动客户端
            // System.out.println("发起连接到："+ia.getHostAddress());
            // TSocketClient.startClient(ia.getHostAddress(),11110);
            
            // BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            // 读取客户端发送来的消息
            while(true){
                // BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                // 写modbus消息
                // ModbusRequest req = new ModBusTcpRequest((short) 1,new ReadHoldingRegisterPayLoad(275,1));
                String value = "1500";
                // 读U16
                // ModbusRequest req = new ModBusTcpRequest((short) 1,new ReadHoldingRegisterPayLoad(5,6));
                // 读U16A
                ModbusRequest req = new ModBusTcpRequest((short) 1,new ReadHoldingRegisterPayLoad(277,24));
                // 读S16
                // ModbusRequest req = new ModBusTcpRequest((short) 1,new ReadHoldingRegisterPayLoad(124,1));
                // 读S32
                // ModbusRequest req = new ModBusTcpRequest((short) 1,new ReadHoldingRegisterPayLoad(116,2));
                // 写U16
                // int arrLength = 20;
                // byte[] bts = new byte[arrLength];
                // String dataType = "U16";
                // // 根据数据类型生成byte数组
                // if (dataType.equals(U16)) { // U16
                //     bts = TranStrTools.int2bytes2(Integer.parseInt(value));
                // } else if (dataType.equals(S16)) { // S16
                //     bts = TranStrTools.int2bytes2(TranStrTools.strTransInt(value));
                // } else if (dataType.equals(S32)) { // S32
                //     bts = TranStrTools.int2bytes4(Integer.parseInt(value));
                // } else if (dataType.equals(U8A)) { // U8A
                //     bts = TranStrTools.str2bytes(value, arrLength);
                // }else if (dataType.equals(U16A)) {  // U16A
                //     // 如果是数组，则拆分之后再组合成bytes

                // }else if (dataType.equals(U32A)) {  // U32A
                // }else if (dataType.equals(S32A)) {  // S32A
                // }

                // 写入思路，直接写入二进制数组
                // System.out.println(Arrays.toString(bts));
                // 写S16
                // ModbusRequest req = new ModBusTcpRequest(new WriteMultipleRegisterPayLoad(273, new MultipleValue(bts, 1)));
                // 写S32
                // ModbusRequest req = new ModBusTcpRequest(new WriteMultipleRegisterPayLoad(257, new MultipleValue(bts, 10)));
                // byte[] bts = TranStrTools.str2bytes(sss, 20);
                // ModbusRequest req = new ModBusTcpRequest(new WriteMultipleRegisterPayLoad(106, new MultipleValue(bts, 10)));
                ByteBuf bf = Unpooled.buffer(12);
                MbMsg.encode(req, bf);
                byte[] bytes = bf.array();
                System.out.println(Arrays.toString(bytes));
                System.out.println(bytes.length);
                out.write(bytes);
                out.flush();
                
                bf = Unpooled.buffer();
                bf.writeBytes(in, 128);
                System.out.println(Arrays.toString(bf.array()));
                // String res = MbMsg.readDataFromRes(bf, "U8A");
                String res = MbMsg.readDataFromRes(bf, "U16A");
                // String res = MbMsg.readDataFromRes(bf, "S16");
                System.out.println("客户端："+res);
                break;
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
    }

    public static void main(String[] args) {
        TSocketServer ts = new TSocketServer();
        ts.startSocketServer(502);
    }
 
}
