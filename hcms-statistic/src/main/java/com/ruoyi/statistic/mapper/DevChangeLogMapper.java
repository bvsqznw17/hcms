package com.ruoyi.statistic.mapper;

import java.util.List;
import com.ruoyi.statistic.domain.DevChangeLog;

/**
 * 设备参数修改日志Mapper接口
 * 
 * @author cbw
 * @date 2022-12-02
 */
public interface DevChangeLogMapper 
{
    /**
     * 查询设备参数修改日志
     * 
     * @param id 设备参数修改日志主键
     * @return 设备参数修改日志
     */
    public DevChangeLog selectDevChangeLogById(Long id);

    /**
     * 查询设备参数修改日志列表
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 设备参数修改日志集合
     */
    public List<DevChangeLog> selectDevChangeLogList(DevChangeLog devChangeLog);

    /**
     * 新增设备参数修改日志
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 结果
     */
    public int insertDevChangeLog(DevChangeLog devChangeLog);

    /**
     * 修改设备参数修改日志
     * 
     * @param devChangeLog 设备参数修改日志
     * @return 结果
     */
    public int updateDevChangeLog(DevChangeLog devChangeLog);

    /**
     * 删除设备参数修改日志
     * 
     * @param id 设备参数修改日志主键
     * @return 结果
     */
    public int deleteDevChangeLogById(Long id);

    /**
     * 批量删除设备参数修改日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDevChangeLogByIds(Long[] ids);
}
