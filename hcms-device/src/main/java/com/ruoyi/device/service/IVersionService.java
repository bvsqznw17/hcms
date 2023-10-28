package com.ruoyi.device.service;

import java.util.List;
import com.ruoyi.device.domain.Version;

/**
 * 软件版本Service接口
 * 
 * @author cbw
 * @date 2022-11-15
 */
public interface IVersionService 
{
    /**
     * 查询软件版本
     * 
     * @param id 软件版本主键
     * @return 软件版本
     */
    public Version selectVersionById(Long id);

    /**
     * 查询软件版本列表
     * 
     * @param Version 软件版本
     * @return 软件版本集合
     */
    public List<Version> selectVersionList(Version Version);

    /**
     * 新增软件版本
     * 
     * @param Version 软件版本
     * @return 结果
     */
    public int insertVersion(Version Version);

    /**
     * 修改软件版本
     * 
     * @param Version 软件版本
     * @return 结果
     */
    public int updateVersion(Version Version);

    /**
     * 批量删除软件版本
     * 
     * @param ids 需要删除的软件版本主键集合
     * @return 结果
     */
    public int deleteVersionByIds(Long[] ids);

    /**
     * 删除软件版本信息
     * 
     * @param id 软件版本主键
     * @return 结果
     */
    public int deleteVersionById(Long id);
}
