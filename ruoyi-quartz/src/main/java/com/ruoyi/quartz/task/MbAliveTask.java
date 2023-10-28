package com.ruoyi.quartz.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.mapper.DevMsgMapper;

/**
 * modbus心跳定期检测
 * 
 * @author cbw
 */
@Component("mbaTask")
public class MbAliveTask {

    @Autowired
    private DevMsgMapper devMsgMapper;

    public void checkDevAlive() {
        System.out.println("执行设备存活检测程序");
        // 获取设备列表
        List<DevMsg> dms = devMsgMapper.selectDevMsgList(null);
        // 取每个设备的更新时间和当前时间进行计算，如果相差10分钟及以上，标记为离线，否则跳过
        // 获取当前时间
        long cur_times = new Date().getTime();
        for (DevMsg dm : dms) {
            if (cur_times - dm.getDtUpdate().getTime() >= 60 * 10 * 1000) {
                dm.setStatus(0);
                devMsgMapper.updateDevMsg(dm);
            }
        }

    }

}
