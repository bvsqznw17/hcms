package com.ruoyi.device.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.device.domain.DevAuth;
import com.ruoyi.device.domain.DevMsg;

public interface IDevAuthSevice {

    // 获取设备树
    public AjaxResult getDevTree(DevAuth devAuth);

    // 根据代理商id获取设备列表
    public AjaxResult listDevByAgentId(DevAuth devAuth);

    // 获取代理商树
    public AjaxResult getAgentTree();

    // 获取代理商对于设备的权限
    public List<DevMsg> listAgentDevAuth(DevAuth devAuth);

    // 获取代理商所管理的用户
    public List<DevAuth> listAgentUser(DevAuth devAuth, SysUser sysUser);

    // 获取用户对于设备的权限
    public List<DevMsg> listUserDevAuth(DevAuth devAuth);

    // 保存代理商对于设备的权限
    public int batchAddDevAuth(List<DevAuth> devAuthlList);

    // 保存用户对于设备的权限
    public int batchAddUserAuth(List<DevAuth> devAuthlList);

    // 删除代理商对于设备的权限
    public int deleteAgentDevAuth(String agentId, String devIds);

    // 删除用户对于设备的权限
    public int deleteUserDevAuth(String userId, String devIds);

    // 添加agent-user记录
    public int addAgentUser(DevAuth devAuth, Long userId);

}
