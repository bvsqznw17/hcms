package com.ruoyi.connDev.modbus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.ParamValueMapper;

/**
 * 定时任务：检测设备数据是否有更新并写入数据库
 */
public class UpdateMbDataToDbTask implements Runnable {

    Logger logger = LoggerFactory.getLogger(UpdateMbDataToDbTask.class);

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
            ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);
            ParamValue pv = new ParamValue();
            pv.setDevName(devName);
            List<ParamValue> pvs = paramValueMapper.selectParamValueList(pv);
            storeToDb(lastStoreData, regLibs, pvs);
        }
    }

    // 存储到数据库
    public void storeToDb(HashMap<String, String> data, List<RegLib> regLibs, List<ParamValue> pvs) {
        // 首先把regLibs和pvs进行比较，得到完整的设备参数
        List<ParamValue> pvs2 = compareData(pvs, regLibs);
        System.out.println("开始存储到数据库");
        // 遍历data,取key和value和pvs对比,如果相同,则生成一个paramValue对象存储到数据库中
        ParamValueMapper paramValueMapper = SpringUtils.getBean(ParamValueMapper.class);
        System.out.println("paramValueMapper:" + data.size());
        for (ParamValue pv : pvs2) {
            String pk = "mb:" + pv.getParamKey();
            ParamValue paramValue = new ParamValue();
            paramValue.setDevName(devName);
            paramValue.setRegAddr(pv.getRegAddr());
            paramValue.setRegNum(pv.getRegNum());
            paramValue.setParamKey(pv.getParamKey());
            paramValue.setParamValue(data.get(pk));
            paramValue.setDtUpdate(new Date());
            // 更新，先检查有没有，没有就插入
            ParamValue pa = getParamValue(pk, pvs);
            if (pa == null) {
                paramValueMapper.insertParamValue(paramValue);
            } else {
                // 比较value，如果不同，就更新
                if (!pa.getParamValue().equals(paramValue.getParamValue())) {
                    paramValueMapper.updateParamValue(paramValue);
                }
            }
        }
    }

    // 比较regLibs和pvs对比
    public List<ParamValue> compareData(List<ParamValue> pvs, List<RegLib> regLibs) {
        List<ParamValue> list = new ArrayList<>();
        // 然后将reg_lib中的参数名和设备参数中的参数名进行比较
        // 如果设备参数中没有这个参数名，那么就添加到设备参数中
        for (RegLib rl : regLibs) {
            boolean flag = true;
            for (ParamValue pv : pvs) {
                if (rl.getParamName().equals(pv.getParamName())) {
                    flag = false;
                    pv.setRegNum(rl.getRegNum());
                    pv.setDataType(rl.getDataType());
                    break;
                }
            }
            if (flag) {
                ParamValue pv = new ParamValue();
                pv.setDevName(devName);
                pv.setParamKey(rl.getParamKey());
                pv.setParamName(rl.getParamName());
                pv.setParamType(rl.getParamType());
                pv.setParamSubType(rl.getParamSubType());
                pv.setRegAddr(rl.getRegAddr());
                pv.setRegNum(rl.getRegNum());
                pv.setDataType(rl.getDataType());
                pv.setMinV(rl.getMinV());
                pv.setMaxV(rl.getMaxV());
                pv.setDecimalNum(getDecimalNum());
                pv.setUnit(rl.getUnit());
                // 根据dataType，设置paramValue的值，如果是U8A，设置""，如果是其他，设置0
                if (rl.getDataType().equals("U8A")) {
                    pv.setParamValue("");
                } else {
                    pv.setParamValue("0");
                }
                pv.setDtUpdate(new Date());
                list.add(pv);
            }

        }
        return list;
    }

    // 获取decimalNum
    private Integer getDecimalNum() {
        String res = lastStoreData.get("mb:sys_dot_num");
        return Integer.parseInt(res);
    }

    // 获取devName的所有paramValue，然后根据传入的参数paramKey来获取对应的paramValue
    private ParamValue getParamValue(String paramKey, List<ParamValue> pvs) {
        // 遍历pvs，找到对应的paramKey的paramValue返回
        for (ParamValue pv : pvs) {
            if (pv.getParamKey().equals(paramKey)) {
                return pv;
            }
        }
        return null;
    }

}
