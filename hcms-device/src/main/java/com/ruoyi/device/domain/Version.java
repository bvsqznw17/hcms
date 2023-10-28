package com.ruoyi.device.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 软件版本对象 hcms_version
 * 
 * @author cbw
 * @date 2022-11-15
 */
@Data
public class Version extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;
    
    /** 软件类型 */
    @Excel(name = "软件类型")
    private String type;

    /** 软件名称 */
    @Excel(name = "软件名称")
    private String name;

    /** 软件名称 */
    @Excel(name = "软件语言")
    private String language;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 下载地址 */
    @Excel(name = "下载地址")
    private String url;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

}
