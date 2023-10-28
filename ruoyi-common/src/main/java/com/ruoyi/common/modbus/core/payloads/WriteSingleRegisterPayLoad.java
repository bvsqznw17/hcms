package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;

public class WriteSingleRegisterPayLoad extends BaseModbusPayLoad{
    public WriteSingleRegisterPayLoad(int address, short value) {
        super(ModbusFCode.WRITE_SINGLE_REGISTER, address, 0,value);
    }
}
