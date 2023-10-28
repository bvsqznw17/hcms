package com.ruoyi.device.service;

import com.ruoyi.common.core.domain.AjaxResult;

public interface IBusinessService {

    // 检测设备是否可以被操作
    public AjaxResult checkDevStatus(String devName, String userId);

    // 更改设备的操作状态
    public int changeDevStatus(String devName, String operateStatus);

    // 给设备发送指令
    public int writeCmd(String devName, String cmd, String cmdParam);

    // 读取设备参数
    public AjaxResult readParam(String devName, String paramKey);

    // 读取一组设备的参数
    public AjaxResult readParams(String devName, String[] paramKeys);

    // 获取设备的运行操作状态
    public AjaxResult getRunStatus(String devName);

    // 获取组合面板的数据
    public AjaxResult getPanelData(String devName);

    // 获取斗状态数据
    public AjaxResult getDouStatus(String devName);

}
