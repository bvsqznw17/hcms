package com.ruoyi.statistic.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 设备参数修改日志对象 hcms_dev_change_log
 * 
 * @author cbw
 * @date 2022-12-02
 */
@Data
public class DevChangeLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String devName;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String devModel;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String paramName;

    /** 参数值 */
    @Excel(name = "参数值")
    private String paramValue;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

    /** 修改内容 */
    @Excel(name = "修改内容")
    private String content;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setOperator(String operator) 
    {
        this.operator = operator;
    }

    public String getOperator() 
    {
        return operator;
    }
    public void setDevName(String devName) 
    {
        this.devName = devName;
    }

    public String getDevName() 
    {
        return devName;
    }
    public void setDevModel(String devModel) 
    {
        this.devModel = devModel;
    }

    public String getDevModel() 
    {
        return devModel;
    }
    public void setParamname(String paramName) 
    {
        this.paramName = paramName;
    }

    public String getParamname() 
    {
        return paramName;
    }
    public void setParamvalue(String paramValue) 
    {
        this.paramValue = paramValue;
    }

    public String getParamvalue() 
    {
        return paramValue;
    }
    public void setDtUpdate(Date dtUpdate) 
    {
        this.dtUpdate = dtUpdate;
    }

    public Date getDtUpdate() 
    {
        return dtUpdate;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("operator", getOperator())
            .append("devName", getDevName())
            .append("devModel", getDevModel())
            .append("paramName", getParamname())
            .append("paramValue", getParamvalue())
            .append("dtUpdate", getDtUpdate())
            .append("content", getContent())
            .toString();
    }
}
