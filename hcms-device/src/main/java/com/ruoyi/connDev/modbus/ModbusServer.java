package com.ruoyi.connDev.modbus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.base.service.IRegLibService;
import com.ruoyi.common.utils.modbus.ModbusUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.service.IDevMsgService;

public class ModbusServer extends Thread {

  private ServerSocket serverSocket = null;
  private int port = 502;
  private static final int TIME_OUT = 10 * 1000;

  public ModbusServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    System.out.println("modbus: 服务器在" + port + "监听..");

    try {
      serverSocket = new ServerSocket(port);

      while (true) {
        Socket socket = serverSocket.accept();
        configureSocket(socket);

        String ip = socket.getInetAddress().getHostAddress();
        String key = getDeviceKey(socket, ip);

        // if (ModbusSocketManage.getHmb().containsKey(key)) {
        // ModbusSocketManage.removeMbServerThread(key);
        // }

        // 如果已经包含了该线程，直接移除该线程重新创建
        if (ModbusSocketManage.getHm().containsKey(key)) {
          ModbusSocketManage.stopThread(key);
        }

        List<RegLib> regLibs = SpringUtils.getBean(IRegLibService.class).selectRegLibList(null);
        // MbServerThread mbServerThread = new MbServerThread(socket, ip, key, regLibs);
        // mbServerThread.start();
        // ModbusSocketManage.addMbServerThread(key, mbServerThread);

        // 创建线程与客户端保持通信
        ModbusServerConClientThread serverConClientThread = new ModbusServerConClientThread(socket, ip,
            key, regLibs);
        serverConClientThread.start();
        // 放入集合
        ModbusSocketManage
            .addServerConClientThread(key, serverConClientThread);
      }
    } catch (Exception e) {
      System.out.println("modbus: 服务器异常");
      e.printStackTrace();
    } finally {
      closeServerSocket();
    }
  }

  private void configureSocket(Socket socket) throws IOException {
    socket.setKeepAlive(true);
    socket.setSoTimeout(TIME_OUT);
  }

  private String getDeviceKey(Socket socket, String ip) throws IOException {
    IDevMsgService devMsgService = SpringUtils.getBean(IDevMsgService.class);
    String uuid = ModbusUtils.getUUID(socket.getOutputStream(), socket.getInputStream());

    DevMsg devMsg = new DevMsg();
    devMsg.setUuid(uuid);
    List<DevMsg> devMsgs = devMsgService.selectDevMsgList(devMsg);

    if (devMsgs.isEmpty()) {
      return ip;
    }

    String devName = devMsgs.get(0).getDevName();
    return (devName == null || devName.equals("")) ? ip : devName;
  }

  private void closeServerSocket() {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
