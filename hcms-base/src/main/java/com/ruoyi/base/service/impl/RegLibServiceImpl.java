package com.ruoyi.base.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.base.mapper.RegLibMapper;
import com.ruoyi.base.domain.RegLib;
import com.ruoyi.base.service.IRegLibService;

/**
 * 基础库配置Service业务层处理
 * 
 * @author cbw
 * @date 2022-10-14
 */
@Service
public class RegLibServiceImpl implements IRegLibService 
{
    @Autowired
    private RegLibMapper RegLibMapper;

    /**
     * 查询基础库配置
     * 
     * @param id 基础库配置主键
     * @return 基础库配置
     */
    @Override
    public RegLib selectRegLibById(Long id)
    {
        return RegLibMapper.selectRegLibById(id);
    }

    /**
     * 查询基础库配置列表
     * 
     * @param RegLib 基础库配置
     * @return 基础库配置
     */
    @Override
    public List<RegLib> selectRegLibList(RegLib RegLib)
    {
        return RegLibMapper.selectRegLibList(RegLib);
    }

    /**
     * 新增基础库配置
     * 
     * @param RegLib 基础库配置
     * @return 结果
     */
    @Override
    public int insertRegLib(RegLib RegLib)
    {
        return RegLibMapper.insertRegLib(RegLib);
    }

    /**
     * 修改基础库配置
     * 
     * @param RegLib 基础库配置
     * @return 结果
     */
    @Override
    public int updateRegLib(RegLib RegLib)
    {
        return RegLibMapper.updateRegLib(RegLib);
    }

    /**
     * 批量删除基础库配置
     * 
     * @param ids 需要删除的基础库配置主键
     * @return 结果
     */
    @Override
    public int deleteRegLibByIds(Long[] ids)
    {
        return RegLibMapper.deleteRegLibByIds(ids);
    }

    /**
     * 删除基础库配置信息
     * 
     * @param id 基础库配置主键
     * @return 结果
     */
    @Override
    public int deleteRegLibById(Long id)
    {
        return RegLibMapper.deleteRegLibById(id);
    }
}
