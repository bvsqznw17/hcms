package com.ruoyi.web.controller.base;

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
import com.ruoyi.base.domain.RegLib;
import com.ruoyi.base.service.IRegLibService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基础库配置Controller
 * 
 * @author cbw
 * @date 2022-10-14
 */
@RestController
@RequestMapping("/base/regLib")
public class RegLibController extends BaseController
{
    @Autowired
    private IRegLibService RegLibService;

    /**
     * 查询基础库配置列表
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:list')")
    @GetMapping("/list")
    public TableDataInfo list(RegLib RegLib)
    {
        startPage();
        List<RegLib> list = RegLibService.selectRegLibList(RegLib);
        return getDataTable(list);
    }

    /**
     * 导出基础库配置列表
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:export')")
    @Log(title = "基础库配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, RegLib RegLib)
    {
        List<RegLib> list = RegLibService.selectRegLibList(RegLib);
        ExcelUtil<RegLib> util = new ExcelUtil<RegLib>(RegLib.class);
        util.exportExcel(response, list, "基础库配置数据");
    }

    /**
     * 获取基础库配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(RegLibService.selectRegLibById(id));
    }

    /**
     * 新增基础库配置
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:add')")
    @Log(title = "基础库配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RegLib RegLib)
    {
        return toAjax(RegLibService.insertRegLib(RegLib));
    }

    /**
     * 修改基础库配置
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:edit')")
    @Log(title = "基础库配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RegLib RegLib)
    {
        return toAjax(RegLibService.updateRegLib(RegLib));
    }

    /**
     * 删除基础库配置
     */
    @PreAuthorize("@ss.hasPermi('base:regLib:remove')")
    @Log(title = "基础库配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(RegLibService.deleteRegLibByIds(ids));
    }
}
