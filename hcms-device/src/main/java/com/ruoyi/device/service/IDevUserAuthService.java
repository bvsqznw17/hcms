package com.ruoyi.device.service;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;

public interface IDevUserAuthService {

    // 根据agentId获取用户列表
    List<SysUser> listUserByAgentId(String agentId);

}
