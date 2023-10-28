package com.ruoyi.device.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.DownloadListMapper;
import com.ruoyi.device.domain.DownloadList;
import com.ruoyi.device.service.IDownloadListService;

/**
 * 下载列表Service业务层处理
 * 
 * @author cbw
 * @date 2022-12-09
 */
@Service
public class DownloadListServiceImpl implements IDownloadListService 
{
    @Autowired
    private DownloadListMapper downloadListMapper;

    /**
     * 查询下载列表
     * 
     * @param id 下载列表主键
     * @return 下载列表
     */
    @Override
    public DownloadList selectDownloadListById(Long id)
    {
        return downloadListMapper.selectDownloadListById(id);
    }

    /**
     * 查询下载列表列表
     * 
     * @param downloadList 下载列表
     * @return 下载列表
     */
    @Override
    public List<DownloadList> selectDownloadListList(DownloadList downloadList)
    {
        return downloadListMapper.selectDownloadListList(downloadList);
    }

    /**
     * 新增下载列表
     * 
     * @param downloadList 下载列表
     * @return 结果
     */
    @Override
    public int insertDownloadList(DownloadList downloadList)
    {
        return downloadListMapper.insertDownloadList(downloadList);
    }

    /**
     * 修改下载列表
     * 
     * @param downloadList 下载列表
     * @return 结果
     */
    @Override
    public int updateDownloadList(DownloadList downloadList)
    {
        return downloadListMapper.updateDownloadList(downloadList);
    }

    /**
     * 批量删除下载列表
     * 
     * @param ids 需要删除的下载列表主键
     * @return 结果
     */
    @Override
    public int deleteDownloadListByIds(Long[] ids)
    {
        return downloadListMapper.deleteDownloadListByIds(ids);
    }

    /**
     * 删除下载列表信息
     * 
     * @param id 下载列表主键
     * @return 结果
     */
    @Override
    public int deleteDownloadListById(Long id)
    {
        return downloadListMapper.deleteDownloadListById(id);
    }
}
