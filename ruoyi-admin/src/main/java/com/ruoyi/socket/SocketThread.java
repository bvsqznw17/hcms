package com.ruoyi.socket;

import java.util.HashMap;

public class SocketThread {
  private static HashMap<String,String> hm = new HashMap<>();

  public static void put(String key,String value){
    hm.put(key,value);
  }

}
