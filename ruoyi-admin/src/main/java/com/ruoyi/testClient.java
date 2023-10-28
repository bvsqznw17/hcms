package com.ruoyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.sign.Md5Utils;

public class testClient {

    private Socket socket;

    private PrintWriter pw;
    private BufferedReader br;

    public testClient(String ip, int port) throws IOException {
        // 主动向服务器发起连接，实现TCP三次握手
        // 不成功则抛出错误，由调用者处理错误
        socket = new Socket(ip, port);

        // 得到网络流输出字节流地址，并封装成网络输出字符流
        OutputStream socketOut = socket.getOutputStream();
        // 参数true表示自动flush数据
        pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);

        // 得到网络输入字节流地址，并封装成网络输入字符流
        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    public void send(String msg) {
        // 输出字符流，由socket调用系统底层函数，经网卡发送字节流
        pw.println(msg);
    }

    public String receive() {
        String msg = null;
        try {
            // 从网络输入字符流中读取信息，每次只能接受一行信息
            // 不够一行时（无行结束符），该语句阻塞
            // 直到条件满足，程序往下运行
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void close() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void keepConnection() {
        try {
            Scanner scanner = new Scanner(System.in);// 装饰标准输入流，用于从控制台输入
            while(true){
                String send = scanner.nextLine();
                pw.println(send);
                String msg = receive();
                System.out.println(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送json请求
     * @return
     * @throws IOException
     */
    private void writeJsonReq() throws IOException{
        // 构建json请求
        JSONObject jo = new JSONObject();
        jo.put("fun", "net_up");
        jo.put("random", "random");
        jo.put("myRandom", "myRandom");
        jo.put("z_content", new JSONObject());
        String joStr = jo.toString();
        String md5 = Md5Utils.hash( joStr);
        // md5 = HexUtil.encodeHexStr(md5);
        String reqStr = joStr + "\0" + md5 + "\0\0";
        System.out.println("发送json请求"+reqStr);
        pw.println(reqStr);
    }

    public static void main(String[] args) {
        try {
            testClient tc = new testClient("127.0.0.1", 8848);
            
            tc.keepConnection();
            // tc.send("hello");
            // tc.writeJsonReq();
            // String msg = "start";
            // System.out.println(msg);
            // while(msg.length() > 0){
            //     msg = tc.receive();
            //     System.out.println(msg);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
