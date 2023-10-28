package com.ruoyi.device.mapper;

import java.util.List;

import com.ruoyi.device.domain.DevAuth;

public interface DevAuthMapper {

    // 获取代理商对于设备的权限
    public List<DevAuth> listAgentDevAuth(String agentId);

    // 获取代理商所管理的用户
    public List<DevAuth> listAgentUser(String agentId);

    // 获取用户对于设备的权限
    public List<DevAuth> listUserDevAuth(String agentId);

    // 根据代理商Id和设备Id检查一条记录是否存在
    public int checkDevAuthExist(DevAuth devAuth);

    // 保存设备权限
    public int saveDevAuth(DevAuth devAuth);

    // 根据userId和设备Id检查一条记录是否存在
    public int checkUserAuthExist(DevAuth devAuth);

    // 保存用户设备权限
    public int saveUserAuth(DevAuth devAuth);

    // 删除设备权限
    public int deleteDevAuthByIds(Long[] devIds);

    // 根据代理商Id和devId删除设备权限
    public int deleteDevAuthByAgentIdAndDevId(DevAuth devAuth);

    // 根据UserId和devId删除设备权限
    public int deleteDevAuthByUserIdAndDevId(DevAuth devAuth);

    // 获取所有终端用户
    public List<DevAuth> listAllTerminalUser();

    // 插入一条agentId和userId的对应记录
    public int insertAgentUser(DevAuth devAuth);

}
