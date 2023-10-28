package com.ruoyi.device.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.device.domain.SysSettings;
import com.ruoyi.device.mapper.SysSettingsMapper;
import com.ruoyi.device.service.ISysSettingsService;

/**
 * 系统设置Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-04-12
 */
@Service
public class SysSettingsServiceImpl implements ISysSettingsService {
    @Autowired
    private SysSettingsMapper SysSettingsMapper;

    /**
     * 查询系统设置
     * 
     * @param id 系统设置主键
     * @return 系统设置
     */
    @Override
    public SysSettings selectSysSettingsById(Long id) {
        return SysSettingsMapper.selectSysSettingsById(id);
    }

    /**
     * 查询系统设置列表
     * 
     * @param SysSettings 系统设置
     * @return 系统设置
     */
    @Override
    public List<SysSettings> selectSysSettingsList(SysSettings SysSettings) {
        return SysSettingsMapper.selectSysSettingsList(SysSettings);
    }

    /**
     * 新增系统设置
     * 
     * @param SysSettings 系统设置
     * @return 结果
     */
    @Override
    public int insertSysSettings(SysSettings SysSettings) {
        return SysSettingsMapper.insertSysSettings(SysSettings);
    }

    /**
     * 修改系统设置
     * 
     * @param SysSettings 系统设置
     * @return 结果
     */
    @Override
    public int updateSysSettings(SysSettings SysSettings) {
        return SysSettingsMapper.updateSysSettings(SysSettings);
    }

    /**
     * 批量删除系统设置
     * 
     * @param ids 需要删除的系统设置主键
     * @return 结果
     */
    @Override
    public int deleteSysSettingsByIds(Long[] ids) {
        return SysSettingsMapper.deleteSysSettingsByIds(ids);
    }

    /**
     * 删除系统设置信息
     * 
     * @param id 系统设置主键
     * @return 结果
     */
    @Override
    public int deleteSysSettingsById(Long id) {
        return SysSettingsMapper.deleteSysSettingsById(id);
    }
}
