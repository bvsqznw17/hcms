package com.ruoyi.connDev.jtt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class testServer {

    private ServerSocket serverSocket = null;
    private int port = 5020;

    public testServer() {

    }

    // 创建指定端口的服务器
    public testServer(int port) {// 构造方法
        this.port = port;// 将方法参数赋值给类参数
    }

    // 提供服务
    public void service() {// 创建service方法
        // 端口可以写在配置文件
        System.out.println("服务器在" + 5020 + "监听..");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // new Thread(new SendNewsThread()).start();
        try {
            while (true) {
                // 当和某个客户端建立连接后，会继续监听
                Socket socket = serverSocket.accept();
                System.out.println("新的设备接入"+socket.getInetAddress().getHostAddress());
                // 读取标识符，这里先用ip代替
                String key = socket.getInetAddress().getHostAddress();
                // 创建线程与客户端保持通信
                JttSeverConClientThread serverConClientThread = new JttSeverConClientThread(socket,
                        key);
                serverConClientThread.start();
                // 放入集合
                JttSocketManage
                        .addServerConClientThread(key, serverConClientThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 如果退出while关闭serverSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {// 主程序方法
        new testServer().service();// 调用 service方法
    }

}
