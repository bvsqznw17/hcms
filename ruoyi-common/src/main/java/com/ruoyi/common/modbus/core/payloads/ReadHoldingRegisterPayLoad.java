package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;

public class ReadHoldingRegisterPayLoad extends BaseModbusPayLoad{
    public ReadHoldingRegisterPayLoad( int address, int amount) {
        super(ModbusFCode.READ_HOLDING_REGISTER, address, amount);
    }
}
