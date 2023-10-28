package com.ruoyi.connDev.jtt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.service.IDevMsgService;

public class JttServer extends Thread {
  private ServerSocket serverSocket = null;
  private int port = 5020;

  public JttServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    // 端口可以写在配置文件
    System.out.println("jtt: 服务器在"+ 5020 +"监听..");
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
        socket.setKeepAlive(true);
        socket.setSoTimeout(10 * 1000);
        String key = socket.getInetAddress().getHostAddress();
        System.out.println("jtt: " + key + "连接成功");
        if (JttSocketManage.getHm().containsKey(key)) {
          JttSocketManage.stopThread(key);
        }
        // 每次启动服务之前先将所有的设备状态置为离线
        IDevMsgService devMsgService = SpringUtils.getBean(IDevMsgService.class);
        devMsgService.resetDevStatus();
        // 创建线程与客户端保持通信
        JttSeverConClientThread serverConClientThread = new JttSeverConClientThread(socket,
            key);
        serverConClientThread.start();
        // 放入集合
        JttSocketManage
            .addServerConClientThread(key, serverConClientThread);
      }
    } catch (IOException e) {
      System.out.println("jtt: 服务器异常");
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

}
