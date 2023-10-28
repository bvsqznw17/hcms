package com.ruoyi.connDev.auth;

import java.util.HashMap;
import java.util.Map;

public class OperateStatusMap {

    // 建立一个map，key为设备名称，value为用户id，用于记录当前设备正在被哪个用户操作
    private static Map<String, String> operateStatusMap = new HashMap<String, String>();

    public static Map<String, String> getOperateStatusMap() {
        return operateStatusMap;
    }

    public static void setOperateStatusMap(Map<String, String> operateStatusMap) {
        OperateStatusMap.operateStatusMap = operateStatusMap;
    }

    public static void addOperateStatusMap(String devName, String userId) {
        operateStatusMap.put(devName, userId);
    }

    public static void removeOperateStatusMap(String devName) {
        operateStatusMap.remove(devName);
    }
}
