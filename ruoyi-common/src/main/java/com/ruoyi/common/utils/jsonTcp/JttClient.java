package com.ruoyi.common.utils.jsonTcp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;//导入IOException类
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;//导入Socket类
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;//导入Scanner类
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.JTcpFunc;
import com.ruoyi.common.utils.sign.Md5Utils;

/**
 * 注意用到的输入输出流DataInputStream和DataOutputStream，成对出现，最好用字节流
 */
// 客户端类
public class JttClient {// 创建公共类
    private String host = "localhost";// 默认连接到本机
    private int port = 5020;// 默认连接到端口8189
    private String random = ""; // 服务端发来的随机字符串
    private String myRandom = UUID.randomUUID().toString().substring(0, 6); // 随机字符串
    private String key = "sk#`~`#CBM5";
    private File tranFile;
    private ReentrantLock lock = new ReentrantLock();

    public JttClient() {

    }

    // 连接到指定的主机和端口
    public JttClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 连接到指定的主机和端口
    public JttClient(String host, int port, File file) {
        this.host = host;
        this.port = port;
        this.tranFile = file;
    }

    public boolean chat() {// chat方法
        try {
            // 连接到服务器
            Socket socket = new Socket(host, port);// 创建Socket类对象
            try {

                Scanner scanner = new Scanner(System.in);// 装饰标准输入流，用于从控制台输入

                while (true) {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();
                    // String send = scanner.nextLine();//读取控制台输入的内容
                    // System.out.println("客户端：" + send);//输出键盘输出内容提示 ，也就是客户端向服务器端发送的消息
                    // 把从控制台得到的信息传送给服务器
                    // out.write("123".getBytes(StandardCharsets.UTF_8));// 将客户端的信息传递给服务器
                    // 接收来自服务器的信息
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    String accpet = new String(buffer, StandardCharsets.UTF_8);
                    System.out.println("服务器：" + accpet);
                    System.out.println(accpet.length());
                    System.out.println(accpet.indexOf("\0\0"));
                    // 1.如果是随机字符串
                    if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") == accpet.indexOf('\0')) {
                        random = accpet.substring(0, accpet.indexOf("\0"));
                        System.out.println("接收到随机字符串" + random);
                        // 2.发送json请求
                        if (!random.equals("")) {
                            System.out.println("开始写请求");
                            // 获取设备信息
                            writeJsonReq(out, JTcpFunc.MAC_INFO, new JSONObject());
                        }
                    }


                    // 3.接收json请求
                    if (accpet.indexOf("\0\0") != -1 && accpet.indexOf("\0\0") != accpet.indexOf('\0')) { // 如果是json字符串-检测是不是以\0-字符串-\0\0结尾
                        System.out.println("接收到json请求");
                        // 截取
                        String joStr = accpet.substring(0, accpet.indexOf("\0"));
                        String md5 = accpet.substring(accpet.indexOf("\0") + 1, accpet.lastIndexOf("\0\0"));
                        // 转换json，提取关键参数并校验
                        JSONObject jo = JSONObject.parseObject(joStr);
                        // 校验random
                        String random_ = jo.getString("random");
                        System.out.println("接收到random:" + random_);
                        if (!random_.equals(myRandom)) {
                            writeJsonRespErr(out, -1, "random错误");
                            return false;
                        }
                        // 更新random
                        random = jo.getString("myRandom");
                        System.out.println("更新random为：" + random);
                        // 校验md5
                        String md5_ = Md5Utils.hash(joStr + key);
                        if (!md5.trim().equals(md5_)) {
                            writeJsonRespErr(out, -1, "MD5签名错误");
                            return false;
                        }
                        // 校验json
                        if (jo.isEmpty()) {
                            writeJsonRespErr(out, -1, "JSON格式错误");
                            return false;
                        }
                        // 检测fun
                        String fun = jo.getString("fun");
                        System.out.println(fun);
                        // if (!fun.equals(JTcpFunc.NET_UP)) {
                        //     writeJsonRespErr(out, -1, "fun错误");
                        //     return false;
                        // }

                        if (fun.equals(JTcpFunc.MAC_INFO)) {
                            writeJsonReq(out, JTcpFunc.NET_UP, new JSONObject());
                        } else if (fun.equals(JTcpFunc.NET_UP)) {
                            // 4.如果error = 0，发送文件
                            int error = jo.getIntValue("error");
                            String message = jo.getString("message");
                            if (message.equals("接收成功")) {
                                return true;
                            }
                            System.out.println(message);
                            if (error == 0) {
                                lock.lock();
                                System.out.println("开始发送文件");
                                sendFile(out, tranFile);
                                lock.unlock();
                                // return false;
                            }
                        }
                    }
                }

            } finally {
                // socket.close();//关闭Socket监听
            }
        } catch (Exception e) {// 捕获异常
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 截取json字符串
     * 
     * @param out
     * @throws IOException
     */

    /**
     * 发送json请求
     * 
     * @return
     * @throws IOException
     */
    public void writeJsonReq(OutputStream out, String funCode, JSONObject jo) throws IOException {
        // 构建json请求
        jo.put("fun", funCode);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", new JSONObject());
        String joStr = jo.toString();
        String md5 = Md5Utils.hash(joStr + key);
        String reqStr = joStr + "\0" + md5 + "\0\0";
        System.out.println("发送json请求" + reqStr);
        out.write(reqStr.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /**
     * 发送json回应
     * 
     * @return
     */
    private void writeJsonResp() {

    }

    /**
     * 发送文件
     * 
     * @param args
     */
    private void sendFile(OutputStream out, File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        String fileName = file.getName();
        // 文件名字节数
        int fnSize = fileName.getBytes(StandardCharsets.UTF_8).length;
        ByteBuffer buffer = ByteBuffer.wrap(int2Bytes(fnSize));

        System.out.println(fnSize);
        out.write(buffer.array());
        // 文件名
        byte[] fileName_ = fileName.getBytes(StandardCharsets.UTF_8);
        out.write(fileName_);
        // 文件内容字节数
        int filesize = (int) (file.length());
        System.out.println(filesize);
        out.write(int2Bytes(filesize));
        // 文件内容
        byte[] fileContent = new byte[fis.available()];
        System.out.println("=====开始传输文件=====");
        fis.read(fileContent);
        byte[] bytes = new byte[10240];
        int len = 0;
        long progress = 0;
        System.out.println(len);
        FileInputStream fis_ = new FileInputStream(file);
        while ((len = fis_.read(bytes, 0, bytes.length)) != -1) {
            out.write(bytes, 0, len);
            // out.flush();
            progress += len;
            System.out.println("| " + (100 * progress / file.length()) + "% |");
        }
        // out.write(fileContent);
        System.out.println("======文件传输成功=====");
        // 检验和
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(fileName_);
        bos.write(fileContent);
        // System.out.println("使用更新后的random："+random);
        bos.write(random.getBytes(StandardCharsets.UTF_8));
        bos.write(key.getBytes(StandardCharsets.UTF_8));
        byte[] ck = Md5Utils.md5b(bos.toByteArray());
        // System.out.println(ck.toString());
        // System.out.println(ck.length);
        // 检验和字节数
        int ckSize = 16;
        out.write(int2Bytes(ckSize));
        out.write(ck);

        fis_.close();
        fis.close();
    }

    /**
     * writeJsonRespErr
     * 
     * @param args
     * @throws IOException
     */
    private void writeJsonRespErr(OutputStream out, int code, String msg) throws IOException {
        System.out.println(msg);
        JSONObject jo = new JSONObject();
        jo.put("fun", "net_up");
        jo.put("error", code);
        jo.put("message", msg);
        jo.put("random", random);
        jo.put("myRandom", myRandom);
        jo.put("z_content", new JSONObject());
        out.write(jo.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 
     * @param args
     */
    private byte[] int2Bytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) (i >> 16);
        bytes[2] = (byte) (i >> 8);
        bytes[3] = (byte) i;
        return bytes;
    }

    public static void main(String[] args) {// 主程序方法
        File file = new File("D:/tmp/pfx-password.txt");
        // new JttClient("localhost", 8189, file).chat();//调用chat方法
        new JttClient("192.168.0.111", 5020, file).chat();// 调用chat方法
    }

    // @Override
    // public void run() {
    // chat();
    // }
}
