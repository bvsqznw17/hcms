package com.ruoyi.common.modbus.core.payloads;

public interface ModbusPayLoad<T> {
    int getCode();
    int getAddress();
    int getAmount();
    T val();
}
