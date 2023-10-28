package com.ruoyi.device.service;

import java.util.List;
import com.ruoyi.device.domain.DevMsg;

/**
 * 设备信息Service接口
 * 
 * @author cbw
 * @date 2022-12-06
 */
public interface IDevMsgService {
    /**
     * 查询设备信息
     * 
     * @param id 设备信息主键
     * @return 设备信息
     */
    public DevMsg selectDevMsgById(Long id);

    /**
     * 查询设备信息列表
     * 
     * @param devMsg 设备信息
     * @return 设备信息集合
     */
    public List<DevMsg> selectDevMsgList(DevMsg devMsg);

    /**
     * 查询设备信息列表-拓展
     * 
     * @param devMsg 设备信息
     * @return 设备信息集合
     */
    public List<DevMsg> selectDevMsgListEx(DevMsg devMsg, Long userId);

    /**
     * 新增设备信息
     * 
     * @param devMsg 设备信息
     * @return 结果
     */
    public int insertDevMsg(DevMsg devMsg);

    /**
     * 修改设备信息
     * 
     * @param devMsg 设备信息
     * @return 结果
     */
    public int updateDevMsg(DevMsg devMsg);

    /**
     * 批量删除设备信息
     * 
     * @param ids 需要删除的设备信息主键集合
     * @return 结果
     */
    public int deleteDevMsgByIds(Long[] ids);

    /**
     * 删除设备信息信息
     * 
     * @param id 设备信息主键
     * @return 结果
     */
    public int deleteDevMsgById(Long id);

    /**
     * 重置设备在线状态
     */
    public void resetDevStatus();

}
