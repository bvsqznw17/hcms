package com.ruoyi.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDevMap {

    // 建立一个静态的map，用于存储用户和设备的对应关系
    private static Map<String, String> userDevMap = new HashMap<String, String>();

    public static Map<String, String> getUserDevMap() {
        return userDevMap;
    }

    public static void setUserDevMap(Map<String, String> userDevMap) {
        UserDevMap.userDevMap = userDevMap;
    }

    public static void put(String key, String value) {
        userDevMap.put(key, value);
    }

    public static String get(String key) {
        return userDevMap.get(key);
    }

    public static void remove(String key) {
        userDevMap.remove(key);
    }

}
