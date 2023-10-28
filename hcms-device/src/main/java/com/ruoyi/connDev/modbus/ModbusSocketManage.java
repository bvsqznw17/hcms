package com.ruoyi.connDev.modbus;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ModbusSocketManage {

  private static HashMap<String, ModbusServerConClientThread> hm = new HashMap<>();

  private static HashMap<String, MbServerThread> hmb = new HashMap<>();

  public static void addServerConClientThread(String devName, ModbusServerConClientThread socket) {
    hm.put(devName, socket);
  }

  public static ModbusServerConClientThread getServerConClientThread(String devName) {
    return hm.get(devName);
  }

  public static void addMbServerThread(String devName, MbServerThread mbServerThread) {
    hmb.put(devName, mbServerThread);
  }

  public static MbServerThread getMbServerThread(String devName) {
    return hmb.get(devName);
  }

  // 移除MbServerThread
  public static void removeMbServerThread(String devName) {
    hmb.remove(devName);
  }

  // 返回在线用户
  public static String getOnLineDevs() {
    String string = "";
    Iterator<String> iterator = hm.keySet().iterator();
    while (iterator.hasNext()) {
      String next = iterator.next();
      string += next + ",";
    }
    return string;
  }

  // 退出线程
  public static void stopThread(String key) {
    // ServerConClientThread serverConClientThread = hm.get(key);
    // Socket socket = serverConClientThread.getSocket();
    // try {
    // socket.close();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // serverConClientThread.interrupt();
    hm.remove(key);
  }

  // 返回集合
  public static HashMap<String, ModbusServerConClientThread> getHm() {
    return hm;
  }

  // 返回mb集合
  public static HashMap<String, MbServerThread> getHmb() {
    return hmb;
  }

  // 返回entryset
  public static Set<Entry<String, ModbusServerConClientThread>> getEntry() {
    return hm.entrySet();
  }

  // 返回keyset
  public static Set<String> getKeySet() {
    return hm.keySet();
  }
}
