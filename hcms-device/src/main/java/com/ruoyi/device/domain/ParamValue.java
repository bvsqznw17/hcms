package com.ruoyi.device.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 设备参数对象 param_value
 * 
 * @author cbw
 * @date 2022-10-14
 */
@Data
public class ParamValue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 参数类型 */
    @Excel(name = "参数类型")
    private Integer paramType;

    /** 参数子类型 */
    @Excel(name = "参数子类型")
    private Integer paramSubType;

    /** 参数key */
    @Excel(name = "参数Key")
    private String paramKey;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String paramName;

    /** 参数值 */
    @Excel(name = "参数值")
    private String paramValue;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String devName;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String devModel;

    /** ip地址 */
    @Excel(name = "ip地址")
    private String ip;

    /** 端口 */
    @Excel(name = "端口")
    private Integer port;

    /** 寄存器地址 */
    @Excel(name = "寄存器地址")
    private Integer regAddr;

    /** 寄存器数量 */
    @Excel(name = "寄存器数量")
    private Integer regNum;

    /** 数据类型 */
    @Excel(name = "数据类型")
    private String dataType;

    /** 最小值 */
    @Excel(name = "最小值")
    private Integer minV;

    /** 最大值 */
    @Excel(name = "最大值")
    private Integer maxV;

    /** 小数位数 */
    @Excel(name = "小数位数")
    private Integer decimalNum;

    /** 小数位数变动类型 */
    private Integer decimalType;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 页面排序--非本表字段 */
    private Integer pageSeq;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

    // 构造方法：用于便捷地使用常用参数创建对象
    public ParamValue(String devName, String paramKey) {
        this.devName = devName;
        this.paramKey = paramKey;
    }

    // 空构造方法
    public ParamValue() {

    }

}
