package com.ruoyi.device.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备型号对象 hcms_device
 * 
 * @author cbw
 * @date 2022-12-03
 */
public class Device extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String devModel;

    /** 设备类型 */
    @Excel(name = "设备类型")
    private String devType;

    /** 编号前缀 */
    @Excel(name = "编号前缀")
    private String prefix;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtCreate;

    /** 最后更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

    /** 累计编号 */
    @Excel(name = "累计编号")
    private Long aggregateValue;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDevModel(String devModel) 
    {
        this.devModel = devModel;
    }

    public String getDevModel() 
    {
        return devModel;
    }
    public void setDevType(String devType) 
    {
        this.devType = devType;
    }

    public String getDevType() 
    {
        return devType;
    }
    public void setPrefix(String prefix) 
    {
        this.prefix = prefix;
    }

    public String getPrefix() 
    {
        return prefix;
    }
    public void setDtCreate(Date dtCreate) 
    {
        this.dtCreate = dtCreate;
    }

    public Date getDtCreate() 
    {
        return dtCreate;
    }
    public void setDtUpdate(Date dtUpdate) 
    {
        this.dtUpdate = dtUpdate;
    }

    public Date getDtUpdate() 
    {
        return dtUpdate;
    }
    public void setAggregateValue(Long aggregateValue) 
    {
        this.aggregateValue = aggregateValue;
    }

    public Long getAggregateValue() 
    {
        return aggregateValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("devModel", getDevModel())
            .append("devType", getDevType())
            .append("prefix", getPrefix())
            .append("dtCreate", getDtCreate())
            .append("dtUpdate", getDtUpdate())
            .append("aggregateValue", getAggregateValue())
            .toString();
    }
}
