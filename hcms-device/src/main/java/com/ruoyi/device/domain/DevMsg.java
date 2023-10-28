package com.ruoyi.device.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 设备信息对象 hcms_dev_msg
 * 
 * @author cbw
 * @date 2022-12-20
 */
@Data
public class DevMsg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 名称 */
    @Excel(name = "设备名称")
    private String devName;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String devModel;

    /** cpu序列号 */
    @Excel(name = "cpu序列号")
    private String devCpusn;

    /** uuid */
    @Excel(name = "uuid")
    private String uuid;

    /** ip地址 */
    @Excel(name = "ip地址")
    private String ip;

    /** 端口 */
    @Excel(name = "端口")
    private Integer port;

    /** 地区 */
    @Excel(name = "地区")
    private String area;

    /** 操作状态 */
    @Excel(name = "操作状态")
    private Integer operateStatus;

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
