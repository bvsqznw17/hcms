package com.ruoyi.web.controller.device;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.device.domain.Language;
import com.ruoyi.device.service.ILanguageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 语言包管理Controller
 * 
 * @author cbw
 * @date 2022-12-20
 */
@RestController
@RequestMapping("/device/language")
public class LanguageController extends BaseController
{
    @Autowired
    private ILanguageService languageService;

    /**
     * 查询语言包管理列表
     */
    @PreAuthorize("@ss.hasPermi('device:language:list')")
    @GetMapping("/list")
    public TableDataInfo list(Language language)
    {
        startPage();
        List<Language> list = languageService.selectLanguageList(language);
        return getDataTable(list);
    }

    /**
     * 导出语言包管理列表
     */
    @PreAuthorize("@ss.hasPermi('device:language:export')")
    @Log(title = "语言包管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Language language)
    {
        List<Language> list = languageService.selectLanguageList(language);
        ExcelUtil<Language> util = new ExcelUtil<Language>(Language.class);
        util.exportExcel(response, list, "语言包管理数据");
    }

    /**
     * 获取语言包管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:language:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(languageService.selectLanguageById(id));
    }

    /**
     * 新增语言包管理
     */
    @PreAuthorize("@ss.hasPermi('device:language:add')")
    @Log(title = "语言包管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Language language)
    {
        return toAjax(languageService.insertLanguage(language));
    }

    /**
     * 修改语言包管理
     */
    @PreAuthorize("@ss.hasPermi('device:language:edit')")
    @Log(title = "语言包管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Language language)
    {
        return toAjax(languageService.updateLanguage(language));
    }

    /**
     * 删除语言包管理
     */
    @PreAuthorize("@ss.hasPermi('device:language:remove')")
    @Log(title = "语言包管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(languageService.deleteLanguageByIds(ids));
    }

    /**
     * 升级到指定版本
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:update')")
    @Log(title = "语言包管理", businessType = BusinessType.UPDATE)
	@PutMapping("/upgradeLanguage")
    public AjaxResult upgradeLanguage(@RequestBody Language language)
    {
        return toAjax(languageService.upgradeLanguage(language));
    }
}
