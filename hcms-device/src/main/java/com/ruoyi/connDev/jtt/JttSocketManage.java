package com.ruoyi.connDev.jtt;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.poi.sl.usermodel.Shadow;

import java.util.Set;

public class JttSocketManage {

  private static HashMap<String, JttSeverConClientThread> hm = new HashMap<>();
  private static HashMap<String, String> shadow = new HashMap<>();

  public static void addServerConClientThread(String devName, JttSeverConClientThread socket) {
    hm.put(devName, socket);
  }

  public static JttSeverConClientThread getServerConClientThread(String key) {
    return hm.get(key);
  }

  // 退出线程
  public static void stopThread(String key) {
    hm.remove(key);
    // 移除有ip的情况下绑定的devName
    hm.remove(shadow.get(key));
    shadow.remove(key);
  }

  // 返回集合
  public static HashMap<String, JttSeverConClientThread> getHm() {
    return hm;
  }

  // 返回影子集合
  public static HashMap<String, String> getShadow() {
    return shadow;
  }

  // 返回entryset
  public static Set<Entry<String, JttSeverConClientThread>> getEntry() {
    return hm.entrySet();
  }

  // 返回keyset
  public static Set<String> getKeySet() {
    return hm.keySet();
  }
}
