package com.ruoyi.device.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.DevListMapper;
import com.ruoyi.device.domain.DevList;
import com.ruoyi.device.service.IDevListService;

/**
 * 设备列表Service业务层处理
 * 
 * @author cbw
 * @date 2022-10-27
 */
@Service
public class DevListServiceImpl implements IDevListService 
{
    @Autowired
    private DevListMapper devListMapper;

    /**
     * 查询设备列表
     * 
     * @param id 设备列表主键
     * @return 设备列表
     */
    @Override
    public DevList selectDevListById(Long id)
    {
        return devListMapper.selectDevListById(id);
    }

    /**
     * 查询设备列表列表
     * 
     * @param devList 设备列表
     * @return 设备列表
     */
    @Override
    public List<DevList> selectDevListList(DevList devList)
    {
        return devListMapper.selectDevListList(devList);
    }

    /**
     * 新增设备列表
     * 
     * @param devList 设备列表
     * @return 结果
     */
    @Override
    public int insertDevList(DevList devList)
    {
        return devListMapper.insertDevList(devList);
    }

    /**
     * 修改设备列表
     * 
     * @param devList 设备列表
     * @return 结果
     */
    @Override
    public int updateDevList(DevList devList)
    {
        return devListMapper.updateDevList(devList);
    }

    /**
     * 批量删除设备列表
     * 
     * @param ids 需要删除的设备列表主键
     * @return 结果
     */
    @Override
    public int deleteDevListByIds(Long[] ids)
    {
        return devListMapper.deleteDevListByIds(ids);
    }

    /**
     * 删除设备列表信息
     * 
     * @param id 设备列表主键
     * @return 结果
     */
    @Override
    public int deleteDevListById(Long id)
    {
        return devListMapper.deleteDevListById(id);
    }
}
