package com.ruoyi.quartz.task;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.base.domain.RegLib;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.mapper.ParamValueMapper;

@Component("apTask")
public class AddPvTask {
    @Autowired
    private DevMsgMapper devMsgMapper;

    @Autowired
    private ParamValueMapper paramValueMapper;

    @Autowired
    private com.ruoyi.base.mapper.RegLibMapper RegLibMapper;

    public void makePvs()
    {
        System.out.println("执行无参方法");
        // 查询设备名称列表
        List<DevMsg> devMsgs = devMsgMapper.selectDevMsgList(null);
        // 根据设备名称列表查询
        
        // 获取基础参数列表
        List<RegLib> reglibs = RegLibMapper.selectRegLibList(null);
        Random random = new Random(1000L);
        for (DevMsg dm : devMsgs){
            for (RegLib regLib : reglibs) {
                // 跳过保留字段
                if (regLib.getParamName().equals("保留"))continue;
                // 跳过系统参数为0的字段
                if (regLib.getParamSubType() == 0)continue;
                // 执行插入
                ParamValue pv = new ParamValue();
                pv.setDevName(dm.getDevName());
                pv.setDevModel(dm.getDevModel());
                pv.setParamKey(regLib.getParamKey());
                pv.setIp(dm.getIp());
                pv.setPort(dm.getPort());
                pv.setRegAddr(regLib.getRegAddr());
                pv.setParamType(regLib.getParamType());
                pv.setParamSubType(regLib.getParamSubType());
                pv.setDtUpdate(new Date());
                pv.setParamName(regLib.getParamName());
                pv.setParamValue(random.nextInt(1000) + "");
                paramValueMapper.insertParamValue(pv);
            }
        }
    }
}
