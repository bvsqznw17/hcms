package com.ruoyi.device.service;

import java.util.List;
import com.ruoyi.device.domain.Language;

/**
 * 语言包管理Service接口
 * 
 * @author cbw
 * @date 2022-12-20
 */
public interface ILanguageService 
{
    /**
     * 查询语言包管理
     * 
     * @param id 语言包管理主键
     * @return 语言包管理
     */
    public Language selectLanguageById(Long id);

    /**
     * 查询语言包管理列表
     * 
     * @param language 语言包管理
     * @return 语言包管理集合
     */
    public List<Language> selectLanguageList(Language language);

    /**
     * 新增语言包管理
     * 
     * @param language 语言包管理
     * @return 结果
     */
    public int insertLanguage(Language language);

    /**
     * 修改语言包管理
     * 
     * @param language 语言包管理
     * @return 结果
     */
    public int updateLanguage(Language language);

    /**
     * 批量删除语言包管理
     * 
     * @param ids 需要删除的语言包管理主键集合
     * @return 结果
     */
    public int deleteLanguageByIds(Long[] ids);

    /**
     * 删除语言包管理信息
     * 
     * @param id 语言包管理主键
     * @return 结果
     */
    public int deleteLanguageById(Long id);

    /**
     * 更新语言包
     * 
     * @param upgrade 更新语言包
     * @return 结果
     */
    public int upgradeLanguage(Language language);
}
