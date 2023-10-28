package com.ruoyi.device.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.VersionMapper;
import com.ruoyi.device.domain.Version;
import com.ruoyi.device.service.IVersionService;

/**
 * 软件版本Service业务层处理
 * 
 * @author cbw
 * @date 2022-11-15
 */
@Service
public class VersionServiceImpl implements IVersionService 
{
    @Autowired
    private VersionMapper VersionMapper;

    /**
     * 查询软件版本
     * 
     * @param id 软件版本主键
     * @return 软件版本
     */
    @Override
    public Version selectVersionById(Long id)
    {
        return VersionMapper.selectVersionById(id);
    }

    /**
     * 查询软件版本列表
     * 
     * @param Version 软件版本
     * @return 软件版本
     */
    @Override
    public List<Version> selectVersionList(Version Version)
    {
        return VersionMapper.selectVersionList(Version);
    }

    /**
     * 新增软件版本
     * 
     * @param Version 软件版本
     * @return 结果
     */
    @Override
    public int insertVersion(Version Version)
    {
        return VersionMapper.insertVersion(Version);
    }

    /**
     * 修改软件版本
     * 
     * @param Version 软件版本
     * @return 结果
     */
    @Override
    public int updateVersion(Version Version)
    {
        return VersionMapper.updateVersion(Version);
    }

    /**
     * 批量删除软件版本
     * 
     * @param ids 需要删除的软件版本主键
     * @return 结果
     */
    @Override
    public int deleteVersionByIds(Long[] ids)
    {
        return VersionMapper.deleteVersionByIds(ids);
    }

    /**
     * 删除软件版本信息
     * 
     * @param id 软件版本主键
     * @return 结果
     */
    @Override
    public int deleteVersionById(Long id)
    {
        return VersionMapper.deleteVersionById(id);
    }
}
