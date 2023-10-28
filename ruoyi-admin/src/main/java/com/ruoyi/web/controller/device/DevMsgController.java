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
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.service.IDevMsgService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备信息Controller
 * 
 * @author cbw
 * @date 2022-12-06
 */
@RestController
@RequestMapping("/device/devMsg")
public class DevMsgController extends BaseController {
    @Autowired
    private IDevMsgService devMsgService;

    /**
     * 查询设备信息列表
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:list')")
    @GetMapping("/list")
    public TableDataInfo list(DevMsg devMsg) {
        startPage();
        List<DevMsg> list = devMsgService.selectDevMsgList(devMsg);
        return getDataTable(list);
    }

    /**
     * 查询设备信息列表-拓展
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:list')")
    @GetMapping("/listEx")
    public TableDataInfo listEx(DevMsg devMsg) {
        startPage();
        List<DevMsg> list = devMsgService.selectDevMsgListEx(devMsg, getUserId());
        return getDataTable(list);
    }

    /**
     * 导出设备信息列表
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:export')")
    @Log(title = "设备信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevMsg devMsg) {
        List<DevMsg> list = devMsgService.selectDevMsgList(devMsg);
        ExcelUtil<DevMsg> util = new ExcelUtil<DevMsg>(DevMsg.class);
        util.exportExcel(response, list, "设备信息数据");
    }

    /**
     * 获取设备信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(devMsgService.selectDevMsgById(id));
    }

    /**
     * 新增设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:add')")
    @Log(title = "设备信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DevMsg devMsg) {
        return toAjax(devMsgService.insertDevMsg(devMsg));
    }

    /**
     * 修改设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:edit')")
    @Log(title = "设备信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DevMsg devMsg) {
        return toAjax(devMsgService.updateDevMsg(devMsg));
    }

    /**
     * 删除设备信息
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:remove')")
    @Log(title = "设备信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(devMsgService.deleteDevMsgByIds(ids));
    }
}
