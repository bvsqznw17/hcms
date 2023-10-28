package com.ruoyi.device.mapper;

import java.util.List;

import com.ruoyi.device.domain.DevFile;

/**
 * 设备文件Mapper接口
 * 
 * @author admin
 * @date 2021-09-14
 */
public interface DevFileMapper 
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
    public DevFile selectDevFileByDevName(String devName);
    
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
    public List<DevFile> selectDevFilePageList(DevFile devFile);

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
     * 删除设备文件
     * 
     * @param id 设备文件ID
     * @return 结果
     */
    public int deleteDevFileById(Long id);

    /**
     * 批量删除设备文件
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDevFileByIds(Long[] ids);
    
    /**
     * TODO 根据devCode和关键字获取devFile
     */
    public List<DevFile> getDevFileByKey(DevFile devFile);
    
    /**
     *TODO 根据ids查询设备文件
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public List<DevFile> selectDevFileByIds(Long[] ids);
}
