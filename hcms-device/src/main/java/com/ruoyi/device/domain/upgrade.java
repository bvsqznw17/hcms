package com.ruoyi.device.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 软件升级对象 hcms_upgrade
 * 
 * @author cbw
 * @date 2022-11-01
 */
@Data
public class upgrade extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String devName;

    /** 设备ip */
    @Excel(name = "设备ip")
    private String ip;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String devModel;

    /** 当前版本 */
    @Excel(name = "当前版本")
    private String version;

    /** 最新版本 */
    @Excel(name = "最新版本")
    private String newVersion;

    /** 语言包 */
    @Excel(name = "语言包")
    private String language;

    /** 下载地址 */
    private String url;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

}
