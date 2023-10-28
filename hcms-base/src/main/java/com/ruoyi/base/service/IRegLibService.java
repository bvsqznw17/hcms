package com.ruoyi.base.service;

import java.util.List;
import com.ruoyi.base.domain.RegLib;

/**
 * 基础库配置Service接口
 * 
 * @author cbw
 * @date 2022-10-14
 */
public interface IRegLibService 
{
    /**
     * 查询基础库配置
     * 
     * @param id 基础库配置主键
     * @return 基础库配置
     */
    public RegLib selectRegLibById(Long id);

    /**
     * 查询基础库配置列表
     * 
     * @param RegLib 基础库配置
     * @return 基础库配置集合
     */
    public List<RegLib> selectRegLibList(RegLib RegLib);

    /**
     * 新增基础库配置
     * 
     * @param RegLib 基础库配置
     * @return 结果
     */
    public int insertRegLib(RegLib RegLib);

    /**
     * 修改基础库配置
     * 
     * @param RegLib 基础库配置
     * @return 结果
     */
    public int updateRegLib(RegLib RegLib);

    /**
     * 批量删除基础库配置
     * 
     * @param ids 需要删除的基础库配置主键集合
     * @return 结果
     */
    public int deleteRegLibByIds(Long[] ids);

    /**
     * 删除基础库配置信息
     * 
     * @param id 基础库配置主键
     * @return 结果
     */
    public int deleteRegLibById(Long id);
}
