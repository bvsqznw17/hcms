package com.ruoyi.web.controller.device;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.device.domain.SysSettings;
import com.ruoyi.device.service.ISysSettingsService;

/**
 * 系统设置Controller
 * 
 * @author ruoyi
 * @date 2023-04-12
 */
@RestController
@RequestMapping("/device/settings")
public class SysSettingsController extends BaseController {

    @Autowired
    private ISysSettingsService SysSettingsService;

    /**
     * 查询系统设置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysSettings SysSettings) {
        startPage();
        List<SysSettings> list = SysSettingsService.selectSysSettingsList(SysSettings);
        return getDataTable(list);
    }

    /**
     * 导出系统设置列表
     */
    @Log(title = "系统设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysSettings SysSettings) {
        List<SysSettings> list = SysSettingsService.selectSysSettingsList(SysSettings);
        ExcelUtil<SysSettings> util = new ExcelUtil<SysSettings>(SysSettings.class);
        util.exportExcel(response, list, "系统设置数据");
    }

    /**
     * 获取系统设置详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(SysSettingsService.selectSysSettingsById(id));
    }

    /**
     * 新增系统设置
     */
    @Log(title = "系统设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysSettings SysSettings) {
        return toAjax(SysSettingsService.insertSysSettings(SysSettings));
    }

    /**
     * 修改系统设置
     */
    @Log(title = "系统设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysSettings SysSettings) {
        return toAjax(SysSettingsService.updateSysSettings(SysSettings));
    }

    /**
     * 删除系统设置
     */
    @Log(title = "系统设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(SysSettingsService.deleteSysSettingsByIds(ids));
    }
}
