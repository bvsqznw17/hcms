package com.ruoyi.device.service;

import java.util.List;

import com.ruoyi.device.domain.SysSettings;

/**
 * 系统设置Service接口
 * 
 * @author ruoyi
 * @date 2023-04-12
 */
public interface ISysSettingsService {
    /**
     * 查询系统设置
     * 
     * @param id 系统设置主键
     * @return 系统设置
     */
    public SysSettings selectSysSettingsById(Long id);

    /**
     * 查询系统设置列表
     * 
     * @param SysSettings 系统设置
     * @return 系统设置集合
     */
    public List<SysSettings> selectSysSettingsList(SysSettings SysSettings);

    /**
     * 新增系统设置
     * 
     * @param SysSettings 系统设置
     * @return 结果
     */
    public int insertSysSettings(SysSettings SysSettings);

    /**
     * 修改系统设置
     * 
     * @param SysSettings 系统设置
     * @return 结果
     */
    public int updateSysSettings(SysSettings SysSettings);

    /**
     * 批量删除系统设置
     * 
     * @param ids 需要删除的系统设置主键集合
     * @return 结果
     */
    public int deleteSysSettingsByIds(Long[] ids);

    /**
     * 删除系统设置信息
     * 
     * @param id 系统设置主键
     * @return 结果
     */
    public int deleteSysSettingsById(Long id);
}
