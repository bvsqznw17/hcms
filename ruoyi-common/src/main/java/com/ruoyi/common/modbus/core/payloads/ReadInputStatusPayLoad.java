package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;

public class ReadInputStatusPayLoad extends BaseModbusPayLoad{
    public ReadInputStatusPayLoad(int address, int amount) {
        super( ModbusFCode.READ_INPUT_STATUS, address, amount);
    }
}
