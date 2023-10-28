package com.ruoyi.device.mapper;

import java.util.List;
import com.ruoyi.device.domain.upgrade;

/**
 * 软件升级Mapper接口
 * 
 * @author cbw
 * @date 2022-11-01
 */
public interface upgradeMapper 
{
    /**
     * 查询软件升级
     * 
     * @param id 软件升级主键
     * @return 软件升级
     */
    public upgrade selectupgradeById(Long id);

    /**
     * 查询软件升级列表
     * 
     * @param upgrade 软件升级
     * @return 软件升级集合
     */
    public List<upgrade> selectupgradeList(upgrade upgrade);

    /**
     * 新增软件升级
     * 
     * @param upgrade 软件升级
     * @return 结果
     */
    public int insertupgrade(upgrade upgrade);

    /**
     * 修改软件升级
     * 
     * @param upgrade 软件升级
     * @return 结果
     */
    public int updateupgrade(upgrade upgrade);

    /**
     * 删除软件升级
     * 
     * @param id 软件升级主键
     * @return 结果
     */
    public int deleteupgradeById(Long id);

    /**
     * 删除软件升级
     * 
     * @param devName 设备名称
     * @return 结果
     */
    public int deleteupgradeByDevName(String devName);

    /**
     * 批量删除软件升级
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteupgradeByIds(Long[] ids);
}
