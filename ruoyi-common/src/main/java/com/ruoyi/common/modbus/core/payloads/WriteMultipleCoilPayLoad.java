package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;
import com.ruoyi.common.modbus.core.value.ModbusValue;

public class WriteMultipleCoilPayLoad extends BaseModbusPayLoad{
    public WriteMultipleCoilPayLoad(int address, ModbusValue<Short> value) {
        super(ModbusFCode.WRITE_MULTIPLE_COIL, address, value.len(), value.value());
    }
}
