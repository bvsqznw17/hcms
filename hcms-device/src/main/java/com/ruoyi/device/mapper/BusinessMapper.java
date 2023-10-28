package com.ruoyi.device.mapper;

public interface BusinessMapper {
    
    // 检测设备是否可以被操作
    public int checkDevStatus(String userModel);
}
