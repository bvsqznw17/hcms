package com.ruoyi.device.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.oss.model.OSSObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.jsonTcp.JttClient;
import com.ruoyi.common.utils.oss.OssUtils;
import com.ruoyi.connDev.jtt.JttSeverConClientThread;
import com.ruoyi.connDev.jtt.JttSocketManage;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.upgrade;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.mapper.upgradeMapper;
import com.ruoyi.device.service.IupgradeService;

import cn.hutool.core.thread.ThreadUtil;

/**
 * 软件升级Service业务层处理
 * 
 * @author cbw
 * @date 2022-11-01
 */
@Service
public class upgradeServiceImpl implements IupgradeService 
{
    @Autowired
    private upgradeMapper upgradeMapper;

    @Autowired
    private DevMsgMapper devMsgMapper;

    /**
     * 查询软件升级
     * 
     * @param id 软件升级主键
     * @return 软件升级
     */
    @Override
    public upgrade selectupgradeById(Long id)
    {
        return upgradeMapper.selectupgradeById(id);
    }

    /**
     * 查询软件升级列表
     * 
     * @param upgrade 软件升级
     * @return 软件升级
     */
    @Override
    public List<upgrade> selectupgradeList(upgrade upgrade)
    {
        return upgradeMapper.selectupgradeList(upgrade);
    }

    /**
     * 新增软件升级
     * 
     * @param upgrade 软件升级
     * @return 结果
     */
    @Override
    public int insertupgrade(upgrade upgrade)
    {
        return upgradeMapper.insertupgrade(upgrade);
    }

    /**
     * 修改软件升级
     * 
     * @param upgrade 软件升级
     * @return 结果
     */
    @Override
    public int updateupgrade(upgrade upgrade)
    {
        return upgradeMapper.updateupgrade(upgrade);
    }

    /**
     * 批量删除软件升级
     * 
     * @param ids 需要删除的软件升级主键
     * @return 结果
     */
    @Override
    public int deleteupgradeByIds(Long[] ids)
    {
        return upgradeMapper.deleteupgradeByIds(ids);
    }

    /**
     * 删除软件升级信息
     * 
     * @param id 软件升级主键
     * @return 结果
     */
    @Override
    public int deleteupgradeById(Long id)
    {
        return upgradeMapper.deleteupgradeById(id);
    }

    @Override
    public int upgradeToNew(upgrade upgrade) {

        String url = upgrade.getUrl();
        // 截取url后缀
        String suffix = url.substring(url.lastIndexOf("/") + 1, url.length());
        File file = new File(RuoYiConfig.getTmpFile() + suffix);
        try {
            OssUtils.downloadOssFile(url, file);
            // 根据devName获取其连接线程，然后调用线程的socket进行文件传输操作
            JttSeverConClientThread jttSeverConClientThread = JttSocketManage.getServerConClientThread(upgrade.getDevName());
            jttSeverConClientThread.setFile(file);
            boolean res = jttSeverConClientThread.startSendFile(file.length());
            // 完成之后更新软件的版本号
            this.updateupgrade(upgrade);
            if(res){
                return 1;
            }else{
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteupgradeByDevName(String devName) {
        return upgradeMapper.deleteupgradeByDevName(devName);
    }

}
