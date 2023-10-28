package com.ruoyi.web.controller.device;

import java.util.Date;
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
import com.ruoyi.device.domain.DevList;
import com.ruoyi.device.service.IDevListService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;

/**
 * 设备列表Controller
 * 
 * @author cbw
 * @date 2022-10-27
 */
@RestController
@RequestMapping("/device/devList")
public class DevListController extends BaseController
{
    @Autowired
    private IDevListService devListService;

    /**
     * 查询设备列表列表
     */
    @PreAuthorize("@ss.hasPermi('device:devList:list')")
    @GetMapping("/list")
    public TableDataInfo list(DevList devList)
    {
        startPage();
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        System.out.println(pageNum+"----"+pageSize);
        List<DevList> list = devListService.selectDevListList(devList);
        return getDataTable(list);
    }

    /**
     * 导出设备列表列表
     */
    @PreAuthorize("@ss.hasPermi('device:devList:export')")
    @Log(title = "设备列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevList devList)
    {
        List<DevList> list = devListService.selectDevListList(devList);
        ExcelUtil<DevList> util = new ExcelUtil<DevList>(DevList.class);
        util.exportExcel(response, list, "设备列表数据");
    }

    /**
     * 获取设备列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:devList:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(devListService.selectDevListById(id));
    }

    /**
     * 新增设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:devList:add')")
    @Log(title = "设备列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DevList devList)
    {
        devList.setDtCreate(new Date());
        devList.setDtUpdate(new Date());
        return toAjax(devListService.insertDevList(devList));
    }

    /**
     * 修改设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:devList:edit')")
    @Log(title = "设备列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DevList devList)
    {
        devList.setDtUpdate(new Date());
        return toAjax(devListService.updateDevList(devList));
    }

    /**
     * 删除设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:devList:remove')")
    @Log(title = "设备列表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(devListService.deleteDevListByIds(ids));
    }
}
