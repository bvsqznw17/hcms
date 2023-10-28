package com.ruoyi.device.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.device.domain.DevAuth;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.mapper.DevAuthMapper;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.service.IDevAuthSevice;
import com.ruoyi.system.mapper.SysUserMapper;

@Service
public class DevAuthServiceImpl implements IDevAuthSevice {

    @Autowired
    private DevMsgMapper devMsgMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private DevAuthMapper devAuthMapper;

    @Override
    public AjaxResult getDevTree(DevAuth devAuth) {
        JSONArray jsonArray = new JSONArray();
        // 1. 获取所有设备
        List<DevMsg> devMsgList = devMsgMapper.selectDevMsgList(null);
        // 2. 按照设备型号分组
        for (DevMsg devMsg : devMsgList) {

        }
        // 3. 返回设备树
        return AjaxResult.success(devMsgList);
    }

    // 根据代理商id获取设备列表
    @Override
    public AjaxResult listDevByAgentId(DevAuth devAuth) {
        // 1. 获取代理商对于设备的权限
        List<DevAuth> devAuthList = devAuthMapper.listAgentDevAuth(devAuth.getAgentId());
        // 2. 获取所有设备
        List<DevMsg> devMsgList = devMsgMapper.selectDevMsgList(null);
        List<DevMsg> devMsgList2 = new ArrayList<DevMsg>();
        // 3. 遍历设备列表，如果设备ID在代理商对于设备的权限列表中，则添加到返回列表中
        for (DevMsg devMsg : devMsgList) {
            for (DevAuth devAuth2 : devAuthList) {
                if (devMsg.getId() == Long.parseLong(devAuth2.getDevId())) {
                    devMsgList2.add(devMsg);
                    break;
                }
            }
        }
        // 4. 返回设备列表
        return AjaxResult.success(devMsgList2);
    }

    @Override
    public AjaxResult getAgentTree() {
        // 获取所有代理商（如果有代理商分级，需要获取所有代理商的上级代理商）
        // 暂时给出简单版本，根据角色获取代理商
        SysUser sysUser = new SysUser();
        sysUser.setRoleId(100L);
        List<SysUser> agentList = sysUserMapper.selectUserList(sysUser);
        return AjaxResult.success(agentList);
    }

    @Override
    public List<DevMsg> listAgentDevAuth(DevAuth da) {
        // 根据代理商ID获取代理商对于设备的权限
        List<DevAuth> devAuthList = devAuthMapper.listAgentDevAuth(da.getAgentId());
        // 根据设备ID获取设备信息
        List<DevMsg> devMsgList = new ArrayList<DevMsg>();
        for (DevAuth devAuth : devAuthList) {
            DevMsg devMsg = devMsgMapper.selectDevMsgById(Long.parseLong(devAuth.getDevId()));
            devMsgList.add(devMsg);
        }
        return devMsgList;
    }

    // 获取代理商所管理的用户
    @Override
    public List<DevAuth> listAgentUser(DevAuth da, SysUser user) {
        // 如果是超级管理员，则获取所有用户
        if (user.isAdmin()) {
            return devAuthMapper.listAllTerminalUser();
        }
        return devAuthMapper.listAgentUser(da.getAgentId());
    }

    @Override
    public List<DevMsg> listUserDevAuth(DevAuth da) {
        // 根据代理商ID获取代理商对于设备的权限
        List<DevAuth> devAuthList = devAuthMapper.listUserDevAuth(da.getUserId());
        // 根据设备ID获取设备信息
        List<DevMsg> devMsgList = new ArrayList<DevMsg>();
        for (DevAuth devAuth : devAuthList) {
            DevMsg devMsg = devMsgMapper.selectDevMsgById(Long.parseLong(devAuth.getDevId()));
            devMsgList.add(devMsg);
        }
        return devMsgList;
    }

    @Override
    public int batchAddDevAuth(List<DevAuth> devAuthlList) {
        // 思路：遍历list，如果设备ID已经存在，则跳过，否则新增
        try {
            for (DevAuth devAuth : devAuthlList) {
                if (devAuthMapper.checkDevAuthExist(devAuth) > 0) {
                    continue;
                }
                devAuthMapper.saveDevAuth(devAuth);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int batchAddUserAuth(List<DevAuth> devAuthlList) {
        // 思路：遍历list，如果设备ID已经存在，则跳过，否则新增
        try {
            for (DevAuth devAuth : devAuthlList) {
                if (devAuthMapper.checkUserAuthExist(devAuth) > 0) {
                    continue;
                }
                devAuthMapper.saveUserAuth(devAuth);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteAgentDevAuth(String agentId, String devIds) {
        // 1.分解devIds为一个数组
        String[] devIdArray = devIds.split(",");
        // 2.遍历数组，删除代理商对于设备的权限
        try {
            for (String devId : devIdArray) {
                DevAuth devAuth = new DevAuth();
                devAuth.setAgentId(agentId);
                devAuth.setDevId(devId);
                devAuthMapper.deleteDevAuthByAgentIdAndDevId(devAuth);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // deleteUserDevAuth
    @Override
    public int deleteUserDevAuth(String userId, String devIds) {
        // 1.分解devIds为一个数组
        String[] devIdArray = devIds.split(",");
        // 2.遍历数组，删除代理商对于设备的权限
        try {
            for (String devId : devIdArray) {
                DevAuth devAuth = new DevAuth();
                devAuth.setAgentId(userId);
                devAuth.setDevId(devId);
                devAuthMapper.deleteDevAuthByAgentIdAndDevId(devAuth);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 在添加用户设备权限的时候，添加一条user-userAuth的关联记录
    @Override
    public int addAgentUser(DevAuth devAuth, Long userId) {
        try {
            devAuth.setAgentId(userId + "");
            // 根据用户名获取userId
            String tUserId = sysUserMapper.selectUserByUserName(devAuth.getUserName()).getUserId().toString();
            devAuth.setUserId(tUserId);
            devAuthMapper.insertAgentUser(devAuth);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
