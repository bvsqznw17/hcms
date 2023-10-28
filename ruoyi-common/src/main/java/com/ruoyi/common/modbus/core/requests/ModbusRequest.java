package com.ruoyi.common.modbus.core.requests;

import com.ruoyi.common.modbus.core.payloads.ModbusPayLoad;

public interface ModbusRequest{
    ModbusPayLoad getPayLoad();

    int getFlag();

    int setFlag(int i);

    short getPool();

    short getUid();

}
