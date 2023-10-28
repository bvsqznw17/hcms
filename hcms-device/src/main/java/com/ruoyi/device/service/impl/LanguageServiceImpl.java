package com.ruoyi.device.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyun.oss.model.OSSObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.jsonTcp.JttClient;
import com.ruoyi.common.utils.oss.OssUtils;
import com.ruoyi.device.domain.Language;
import com.ruoyi.device.mapper.LanguageMapper;
import com.ruoyi.device.service.ILanguageService;

import cn.hutool.core.thread.ThreadUtil;

/**
 * 语言包管理Service业务层处理
 * 
 * @author cbw
 * @date 2022-12-20
 */
@Service
public class LanguageServiceImpl implements ILanguageService 
{
    @Autowired
    private LanguageMapper languageMapper;

    /**
     * 查询语言包管理
     * 
     * @param id 语言包管理主键
     * @return 语言包管理
     */
    @Override
    public Language selectLanguageById(Long id)
    {
        return languageMapper.selectLanguageById(id);
    }

    /**
     * 查询语言包管理列表
     * 
     * @param language 语言包管理
     * @return 语言包管理
     */
    @Override
    public List<Language> selectLanguageList(Language language)
    {
        return languageMapper.selectLanguageList(language);
    }

    /**
     * 新增语言包管理
     * 
     * @param language 语言包管理
     * @return 结果
     */
    @Override
    public int insertLanguage(Language language)
    {
        return languageMapper.insertLanguage(language);
    }

    /**
     * 修改语言包管理
     * 
     * @param language 语言包管理
     * @return 结果
     */
    @Override
    public int updateLanguage(Language language)
    {
        return languageMapper.updateLanguage(language);
    }

    /**
     * 批量删除语言包管理
     * 
     * @param ids 需要删除的语言包管理主键
     * @return 结果
     */
    @Override
    public int deleteLanguageByIds(Long[] ids)
    {
        return languageMapper.deleteLanguageByIds(ids);
    }

    /**
     * 删除语言包管理信息
     * 
     * @param id 语言包管理主键
     * @return 结果
     */
    @Override
    public int deleteLanguageById(Long id)
    {
        return languageMapper.deleteLanguageById(id);
    }

    @Override
    public int upgradeLanguage(Language language) {
        String url = language.getUrl();
        // 根据url下载文件
        OSSObject osj = OssUtils.downloadOssObject(url);
        // 截取url后缀
        String suffix = url.substring(url.lastIndexOf("/") + 1, url.length());
        File file = new File(RuoYiConfig.getTmpFile() + suffix);
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while((read = osj.getObjectContent().read(bytes)) != -1){
                os.write(bytes, 0, read);
            }
            // 获取设备的ip和端口
            JttClient jttClient = new JttClient(language.getIp(), RuoYiConfig.getJsport().intValue(), file);
            ThreadUtil.execAsync(()-> jttClient.chat());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
