package com.ruoyi.base.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 基础库配置对象 hcms_reg_lib
 * 
 * @author cbw
 * @date 2022-10-14
 */
@Data
public class RegLib extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 参数类型 */
    @Excel(name = "设备型号")
    private String devModel;

    /** 参数类型 */
    @Excel(name = "参数大类")
    private Integer paramType;

    /** 参数子类型 */
    @Excel(name = "参数子类型")
    private Integer paramSubType;

    /** 参数Key */
    @Excel(name = "参数Key")
    private String paramKey;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String paramName;

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

    /** 读写性 */
    @Excel(name = "读写性")
    private Integer operateType;

    /** 页面序号 */
    private Integer pageSeq;

}
