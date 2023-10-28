package com.ruoyi.device.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

@Data
public class DevAuth extends BaseEntity {

    // 代理商ID
    private String agentId;

    // 设备ID
    private String devId;

    // 用户ID
    private String userId;

    // 用户名
    private String userName;
}
