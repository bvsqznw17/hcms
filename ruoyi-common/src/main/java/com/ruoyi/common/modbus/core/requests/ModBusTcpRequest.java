package com.ruoyi.common.modbus.core.requests;

import com.ruoyi.common.modbus.core.payloads.ModbusPayLoad;

public class ModBusTcpRequest extends BaseModbusRequest {
    public ModBusTcpRequest(ModbusPayLoad payLoad) {
        super(payLoad);
    }

    public ModBusTcpRequest(Short uid,ModbusPayLoad payLoad) {
        super(uid,payLoad);
    }

}
