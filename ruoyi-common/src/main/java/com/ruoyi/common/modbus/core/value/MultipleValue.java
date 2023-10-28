package com.ruoyi.common.modbus.core.value;

public class MultipleValue extends BaseModbusValue{
    
    public MultipleValue(short... values){
        this.len = values.length;
        this.value = values;
    }

    public MultipleValue(byte[] values, int len){
        this.len = len;
        this.value = values;
    }

}
