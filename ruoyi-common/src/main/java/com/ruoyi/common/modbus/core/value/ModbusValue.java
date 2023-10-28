package com.ruoyi.common.modbus.core.value;

public interface ModbusValue<T> {
    T value();

    int len();

}
