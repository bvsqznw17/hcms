package com.ruoyi.device.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 设备列表对象 hcms_dev_list
 * 
 * @author cbw
 * @date 2022-10-27
 */
@Data
public class DevList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** ip地址 */
    @Excel(name = "ip地址")
    private String ip;

    /** 端口 */
    @Excel(name = "端口")
    private Integer port;

    /** 地区 */
    @Excel(name = "地区")
    private String area;

    /** 通信状态 */
    @Excel(name = "通信状态")
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtCreate;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date dtUpdate;

    /** 注释 */
    @Excel(name = "注释")
    private String comment;
}
