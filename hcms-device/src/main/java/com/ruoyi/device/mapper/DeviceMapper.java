package com.ruoyi.device.mapper;

import java.util.List;
import com.ruoyi.device.domain.Device;

/**
 * 设备型号Mapper接口
 * 
 * @author cbw
 * @date 2022-12-03
 */
public interface DeviceMapper 
{
    /**
     * 查询设备型号
     * 
     * @param id 设备型号主键
     * @return 设备型号
     */
    public Device selectDeviceById(Long id);

    /**
     * 查询设备型号列表
     * 
     * @param device 设备型号
     * @return 设备型号集合
     */
    public List<Device> selectDeviceList(Device device);

    /**
     * 新增设备型号
     * 
     * @param device 设备型号
     * @return 结果
     */
    public int insertDevice(Device device);

    /**
     * 修改设备型号
     * 
     * @param device 设备型号
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 删除设备型号
     * 
     * @param id 设备型号主键
     * @return 结果
     */
    public int deleteDeviceById(Long id);

    /**
     * 批量删除设备型号
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceByIds(Long[] ids);
}
