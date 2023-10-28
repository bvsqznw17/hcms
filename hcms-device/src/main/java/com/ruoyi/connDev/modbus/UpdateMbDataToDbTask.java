package com.ruoyi.connDev.modbus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.ParamValueMapper;

/**
 * 定时任务：检测设备数据是否有更新并写入数据库
 */
public class UpdateMbDataToDbTask implements Runnable {

    private String devName = null;// 与服务端通信的设备名称
    private List<RegLib> regLibs = null;
    private HashMap<String, String> lastStoreData = new HashMap<>();

    // 构造器
    public UpdateMbDataToDbTask(String devName, List<RegLib> regLibs, HashMap<String, String> lastStoreData) {
        this.devName = devName;
        this.regLibs = regLibs;
        this.lastStoreData = lastStoreData;
    }

    @Override
    public void run() {
        System.out.println("启动定时任务： 检测设备数据是否有更新并写入数据库");
        // 遍历这些数据并存储到数据库
        if (lastStoreData.size() > 0) {
            storeToDb(lastStoreData, regLibs);
        }
    }

    // 存储到数据库
    public void storeToDb(HashMap<String, String> data, List<RegLib> regLibs) {
        System.out.println("开始存储到数据库");
        // 遍历data,取key和value和regLibs对比,如果相同,则生成一个paramValue对象存储到数据库中
        ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            for (RegLib regLib : regLibs) {
                if (key.contains(regLib.getParamKey())) {
                    ParamValue paramValue = new ParamValue();
                    paramValue.setDevName(devName);
                    paramValue.setRegAddr(regLib.getRegAddr());
                    paramValue.setRegNum(regLib.getRegNum());
                    paramValue.setParamKey(regLib.getParamKey());
                    paramValue.setParamValue(value);
                    paramValue.setDtUpdate(new Date());
                    // 更新，先检查有没有，没有就插入
                    ParamValue pv = paramValueMapper.getParamValueByDevNameAndRegAddr(devName,
                            regLib.getRegAddr() + "");
                    if (pv == null) {
                        paramValueMapper.insertParamValue(paramValue);
                    } else {
                        paramValueMapper.updateParamValue(paramValue);
                    }
                }
            }
        }
    }

}
