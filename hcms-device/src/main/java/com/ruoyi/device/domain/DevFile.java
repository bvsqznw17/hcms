package com.ruoyi.device.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import lombok.Data;

/**
 * 设备文件对象 dev_file
 * 
 * @author adminpackage com.ruoyi.device.domain
 
 * @date 2021-09-14
 */
@Data
public class DevFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "设备型号")
    private String devModel;

    @Excel(name = "设备编码")
    private String devName;

    @Excel(name = "文件业务类型")
    private String businessType;

    @Excel(name = "文件类型")
    private String fileType;

    @Excel(name = "文件名")
    private String fileName;

    @Excel(name = "文件路径")
    private String filePath;
    
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date uploadTime;
    
    private String exParam;

    private Integer perm;

}
