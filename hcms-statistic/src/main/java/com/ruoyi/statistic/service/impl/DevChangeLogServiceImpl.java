package com.ruoyi.statistic.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.statistic.mapper.DevChangeLogMapper;
import com.ruoyi.statistic.domain.DevChangeLog;
import com.ruoyi.statistic.service.IDevChangeLogService;

/**
 * 设备参数修改日志Service业务层处理
 * 
 * @author cbw
 * @date 2022-12-02
 */
@Service
public class DevChangeLogServiceImpl implements IDevChangeLogService 
{
    @Autowired
    private DevChangeLogMapper devChangeLogMapper;

    /**
     * 查询设备参数修改日志
     * 
     * @param id 设备参数修改日志主键
     * @return 设备参数修改日志
     */
    @Override
    public DevChangeLog selectDevChangeLogById(Long id)
    {
        return devChangeLogMapper.selectDevChangeLogById(id);
    }

    /**
     * 查询设备参数修改日志列表
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 设备参数修改日志
     */
    @Override
    public List<DevChangeLog> selectDevChangeLogList(DevChangeLog devChangeLog)
    {
        return devChangeLogMapper.selectDevChangeLogList(devChangeLog);
    }

    /**
     * 新增设备参数修改日志
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 结果
     */
    @Override
    public int insertDevChangeLog(DevChangeLog devChangeLog)
    {
        return devChangeLogMapper.insertDevChangeLog(devChangeLog);
    }

    /**
     * 修改设备参数修改日志
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 结果
     */
    @Override
    public int updateDevChangeLog(DevChangeLog devChangeLog)
    {
        return devChangeLogMapper.updateDevChangeLog(devChangeLog);
    }

    /**
     * 批量删除设备参数修改日志
     * 
     * @param ids 需要删除的设备参数修改日志主键
     * @return 结果
     */
    @Override
    public int deleteDevChangeLogByIds(Long[] ids)
    {
        return devChangeLogMapper.deleteDevChangeLogByIds(ids);
    }

    /**
     * 删除设备参数修改日志信息
     * 
     * @param id 设备参数修改日志主键
     * @return 结果
     */
    @Override
    public int deleteDevChangeLogById(Long id)
    {
        return devChangeLogMapper.deleteDevChangeLogById(id);
    }
}
