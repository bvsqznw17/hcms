package com.ruoyi.web.controller.statistic;

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
import com.ruoyi.statistic.domain.DevChangeLog;
import com.ruoyi.statistic.service.IDevChangeLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备参数修改日志Controller
 * 
 * @author cbw
 * @date 2022-12-02
 */
@RestController
@RequestMapping("/statistic/devChangeLog")
public class DevChangeLogController extends BaseController
{
    @Autowired
    private IDevChangeLogService devChangeLogService;

    /**
     * 查询设备参数修改日志列表
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DevChangeLog devChangeLog)
    {
        startPage();
        List<DevChangeLog> list = devChangeLogService.selectDevChangeLogList(devChangeLog);
        return getDataTable(list);
    }

    /**
     * 导出设备参数修改日志列表
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:export')")
    @Log(title = "设备参数修改日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevChangeLog devChangeLog)
    {
        List<DevChangeLog> list = devChangeLogService.selectDevChangeLogList(devChangeLog);
        ExcelUtil<DevChangeLog> util = new ExcelUtil<DevChangeLog>(DevChangeLog.class);
        util.exportExcel(response, list, "设备参数修改日志数据");
    }

    /**
     * 获取设备参数修改日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(devChangeLogService.selectDevChangeLogById(id));
    }

    /**
     * 新增设备参数修改日志
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:add')")
    @Log(title = "设备参数修改日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DevChangeLog devChangeLog)
    {
        return toAjax(devChangeLogService.insertDevChangeLog(devChangeLog));
    }

    /**
     * 修改设备参数修改日志
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:edit')")
    @Log(title = "设备参数修改日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DevChangeLog devChangeLog)
    {
        return toAjax(devChangeLogService.updateDevChangeLog(devChangeLog));
    }

    /**
     * 删除设备参数修改日志
     */
    @PreAuthorize("@ss.hasPermi('statistic:devChangeLog:remove')")
    @Log(title = "设备参数修改日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(devChangeLogService.deleteDevChangeLogByIds(ids));
    }
}
