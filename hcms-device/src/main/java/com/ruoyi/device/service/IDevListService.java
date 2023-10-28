package com.ruoyi.device.service;

import java.util.List;
import com.ruoyi.device.domain.DevList;

/**
 * 设备列表Service接口
 * 
 * @author cbw
 * @date 2022-10-27
 */
public interface IDevListService 
{
    /**
     * 查询设备列表
     * 
     * @param id 设备列表主键
     * @return 设备列表
     */
    public DevList selectDevListById(Long id);

    /**
     * 查询设备列表列表
     * 
     * @param devList 设备列表
     * @return 设备列表集合
     */
    public List<DevList> selectDevListList(DevList devList);

    /**
     * 新增设备列表
     * 
     * @param devList 设备列表
     * @return 结果
     */
    public int insertDevList(DevList devList);

    /**
     * 修改设备列表
     * 
     * @param devList 设备列表
     * @return 结果
     */
    public int updateDevList(DevList devList);

    /**
     * 批量删除设备列表
     * 
     * @param ids 需要删除的设备列表主键集合
     * @return 结果
     */
    public int deleteDevListByIds(Long[] ids);

    /**
     * 删除设备列表信息
     * 
     * @param id 设备列表主键
     * @return 结果
     */
    public int deleteDevListById(Long id);
}
