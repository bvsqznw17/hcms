package com.ruoyi.common.modbus.core.payloads;

import com.ruoyi.common.modbus.core.typed.ModbusFCode;

public class ReadCoilStatusPayLoad extends BaseModbusPayLoad{
    public ReadCoilStatusPayLoad( int address, int amount) {
        super(ModbusFCode.READ_COIL_STATUS, address, amount);
    }


}
