package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;

public class ReadInputRegisterPayLoad  extends BaseModbusPayLoad{
    public ReadInputRegisterPayLoad( int address, int amount) {
        super(ModbusFCode.READ_INPUT_REGISTER, address, amount);
    }
}
