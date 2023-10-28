package com.ruoyi.device.mapper;

import java.util.List;

import com.ruoyi.device.domain.SysSettings;

/**
 * 系统设置Mapper接口
 * 
 * @author ruoyi
 * @date 2023-04-12
 */
public interface SysSettingsMapper {
    /**
     * 查询系统设置
     * 
     * @param id 系统设置主键
     * @return 系统设置
     */
    public SysSettings selectSysSettingsById(Long id);

    // 根据devName查询
    public SysSettings getSysSettingsByDevName(String devName);

    // 根据devName和paramKey查询
    public SysSettings getSysSettingsByDevNameAndParamKey(SysSettings sysSettings);

    /**
     * 查询系统设置列表
     * 
     * @param hcmsSysSettings 系统设置
     * @return 系统设置集合
     */
    public List<SysSettings> selectSysSettingsList(SysSettings hcmsSysSettings);

    /**
     * 新增系统设置
     * 
     * @param hcmsSysSettings 系统设置
     * @return 结果
     */
    public int insertSysSettings(SysSettings hcmsSysSettings);

    /**
     * 修改系统设置
     * 
     * @param hcmsSysSettings 系统设置
     * @return 结果
     */
    public int updateSysSettings(SysSettings hcmsSysSettings);

    /**
     * 删除系统设置
     * 
     * @param id 系统设置主键
     * @return 结果
     */
    public int deleteSysSettingsById(Long id);

    /**
     * 批量删除系统设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysSettingsByIds(Long[] ids);
}
