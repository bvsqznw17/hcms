package com.ruoyi.device.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 语言包管理对象 hcms_language
 * 
 * @author cbw
 * @date 2022-12-20
 */
public class Language extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String devName;

    /** 设备ip */
    @Excel(name = "设备ip")
    private String ip;

    /** 语言包名 */
    @Excel(name = "语言包名")
    private String name;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 最新版本 */
    @Excel(name = "最新版本")
    private String newVersion;

    /** 下载地址 */
    @Excel(name = "下载地址")
    private String url;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dtUpdate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDevName(String devName) 
    {
        this.devName = devName;
    }

    public String getDevName() 
    {
        return devName;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }
    public void setNewVersion(String newVersion) 
    {
        this.newVersion = newVersion;
    }

    public String getNewVersion() 
    {
        return newVersion;
    }
    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }
    public void setDtUpdate(Date dtUpdate) 
    {
        this.dtUpdate = dtUpdate;
    }

    public Date getDtUpdate() 
    {
        return dtUpdate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("devName", getDevName())
            .append("ip", getIp())
            .append("name", getName())
            .append("version", getVersion())
            .append("newVersion", getNewVersion())
            .append("url", getUrl())
            .append("dtUpdate", getDtUpdate())
            .toString();
    }
}
