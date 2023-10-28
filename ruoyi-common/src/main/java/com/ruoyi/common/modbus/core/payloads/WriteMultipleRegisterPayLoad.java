package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;
import com.ruoyi.common.modbus.core.value.ModbusValue;

public class WriteMultipleRegisterPayLoad extends BaseModbusPayLoad{
    // public WriteMultipleRegisterPayLoad( int address, ModbusValue<short[]> value) {
    //     super(ModbusFCode.WRITE_MULTIPLE_REGISTER, address, value.len(), value.value());
    // }
    public WriteMultipleRegisterPayLoad( int address, ModbusValue<byte[]> value) {
        super(ModbusFCode.WRITE_MULTIPLE_REGISTER, address, value.len(), value.value());
    }
}
