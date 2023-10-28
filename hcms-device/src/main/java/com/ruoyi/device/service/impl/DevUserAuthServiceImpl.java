package com.ruoyi.device.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.device.mapper.DevUserAuthMapper;
import com.ruoyi.device.service.IDevUserAuthService;

@Service
public class DevUserAuthServiceImpl implements IDevUserAuthService {

    @Autowired
    private DevUserAuthMapper devUserAuthMapper;

    @Override
    public List<SysUser> listUserByAgentId(String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listUserByAgentId'");
    }

}
