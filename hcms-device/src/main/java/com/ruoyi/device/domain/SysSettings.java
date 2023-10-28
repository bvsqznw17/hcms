package com.ruoyi.device.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 系统设置对象 hcms_sys_settings
 * 
 * @author ruoyi
 * @date 2023-04-12
 */
@Data
public class SysSettings extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String devName;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String paramKey;

    /** 参数值 */
    @Excel(name = "参数值")
    private String paramValue;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dtUpdate;

}
