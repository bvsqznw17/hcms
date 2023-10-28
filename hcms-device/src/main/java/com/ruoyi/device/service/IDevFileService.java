package com.ruoyi.device.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.device.domain.DevFile;

/**
 * 设备文件Service接口
 * 
 * @author admin
 * @date 2021-09-14
 */
public interface IDevFileService 
{
    /**
     * 查询设备文件
     * 
     * @param id 设备文件ID
     * @return 设备文件
     */
    public DevFile selectDevFileById(Long id);
    
    /**
     * TODO 根据设备编码查找设备文件
     * @param devCode
     * @return
     */
    public DevFile selectDevFileByDevCode(String devCode);

    /**
     * 查询设备文件列表
     * 
     * @param devFile 设备文件
     * @return 设备文件集合
     */
    public List<DevFile> selectDevFileList(DevFile devFile);
    
    /**
     * 查询设备文件页面展示的文件列表
     * 
     * @param devFile 设备文件
     * @return 设备文件集合
     */
    public List<HashMap<String, Object>> selectDevFilePageList(DevFile devFile);

    /**
     * 新增设备文件
     * 
     * @param devFile 设备文件
     * @return 结果
     */
    public int insertDevFile(DevFile devFile);

    /**
     * 修改设备文件
     * 
     * @param devFile 设备文件
     * @return 结果
     */
    public int updateDevFile(DevFile devFile);

    /**
     * 批量删除设备文件
     * 
     * @param ids 需要删除的设备文件ID
     * @return 结果
     */
    public int deleteDevFileByIds(Long[] ids);

    /**
     * 删除设备文件信息
     * 
     * @param id 设备文件ID
     * @return 结果
     */
    public int deleteDevFileById(Long id);
    
    /**
     * TODO 通过devCode、businessType、fileType获取文件路径
     */
    public List<String> getDevFilePaths(String devCode, String businessType, String fileType);
    
    /**
     * TODO 根据devCode和关键字获取devFile
     */
    public List<DevFile> getDevFileByKey(String devCode, String keyWords);
    
    /**
     * TODO 批量下载设备文件
     */
    public void batchDownloadDevFile(HttpServletResponse resp, Long[] ids);
}
