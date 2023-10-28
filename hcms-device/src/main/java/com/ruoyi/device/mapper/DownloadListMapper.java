package com.ruoyi.device.mapper;

import java.util.List;
import com.ruoyi.device.domain.DownloadList;

/**
 * 下载列表Mapper接口
 * 
 * @author cbw
 * @date 2022-12-09
 */
public interface DownloadListMapper 
{
    /**
     * 查询下载列表
     * 
     * @param id 下载列表主键
     * @return 下载列表
     */
    public DownloadList selectDownloadListById(Long id);

    /**
     * 查询下载列表列表
     * 
     * @param downloadList 下载列表
     * @return 下载列表集合
     */
    public List<DownloadList> selectDownloadListList(DownloadList downloadList);

    /**
     * 新增下载列表
     * 
     * @param downloadList 下载列表
     * @return 结果
     */
    public int insertDownloadList(DownloadList downloadList);

    /**
     * 修改下载列表
     * 
     * @param downloadList 下载列表
     * @return 结果
     */
    public int updateDownloadList(DownloadList downloadList);

    /**
     * 删除下载列表
     * 
     * @param id 下载列表主键
     * @return 结果
     */
    public int deleteDownloadListById(Long id);

    /**
     * 批量删除下载列表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDownloadListByIds(Long[] ids);
}
