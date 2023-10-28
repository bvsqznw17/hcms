package com.ruoyi.connDev.modbus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

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
    // 端口可以写在配置文件
    System.out.println("modbus: 服务器在" + port + "监听..");
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
        socket.setSoTimeout(TIME_OUT);
        String ip = socket.getInetAddress().getHostAddress();
        String key = ip;
        // 使用一个while循环等待获取devName作为key
        DevMsg devMsg = new DevMsg();
        // 获取uuid
        String uuid = ModbusUtils.getUUID(socket.getOutputStream(), socket.getInputStream());
        devMsg.setUuid(uuid);
        System.out.println(uuid);
        int limit = 0;
        IDevMsgService devMsgService = SpringUtils.getBean(IDevMsgService.class);
        while (devMsgService.selectDevMsgList(devMsg).size() <= 0 && limit < 15) {
          Thread.sleep(2000);
          limit++;
        }
        // 如果循环之后还是未能获取到devName，直接跳过该设备
        if (devMsgService.selectDevMsgList(devMsg).size() <= 0) {
          continue;
        } else {
          String devName = devMsgService.selectDevMsgList(devMsg).get(0).getDevName();
          if (devName == null || devName.equals("")) {
            key = ip;
          } else {
            key = devName;
          }
        }

        // 如果已经包含了该线程，直接移除该线程重新创建
        if (ModbusSocketManage.getHm().containsKey(key)) {
          ModbusSocketManage.stopThread(key);
        }
        // if (ModbusSocketManage.getHmb().containsKey(key)) {
        // ModbusSocketManage.removeMbServerThread(key);
        // }
        // 从数据库获取reglibs
        List<RegLib> regLibs = SpringUtils.getBean(IRegLibService.class).selectRegLibList(null);
        // 创建线程与客户端保持通信
        ModbusServerConClientThread serverConClientThread = new ModbusServerConClientThread(socket, ip,
            key, regLibs);
        serverConClientThread.start();
        // 放入集合
        ModbusSocketManage
            .addServerConClientThread(key, serverConClientThread);
        // MbServerThread mbServerThread = new MbServerThread(socket, ip, key, regLibs);
        // mbServerThread.start();
        // // 放入集合
        // ModbusSocketManage
        // .addMbServerThread(key, mbServerThread);
      }
    } catch (Exception e) {
      System.out.println("modbus: 服务器异常");
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
