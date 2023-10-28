package com.ruoyi.web.controller.device;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.domain.upgrade;
import com.ruoyi.device.service.IDevMsgService;
import com.ruoyi.device.service.IupgradeService;

/**
 * 软件升级Controller
 * 
 * @author cbw
 * @date 2022-11-01
 */
@RestController
@RequestMapping("/device/upgrade")
public class upgradeController extends BaseController
{
    @Autowired
    private IupgradeService upgradeService;

    @Autowired
    private IDevMsgService devMsgService;

    /**
     * 查询软件升级列表
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:list')")
    @GetMapping("/list")
    public TableDataInfo list(upgrade upgrade)
    {
        startPage();
        List<upgrade> list = upgradeService.selectupgradeList(upgrade);
        return getDataTable(list);
    }

    /**
     * 导出软件升级列表
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:export')")
    @Log(title = "软件升级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, upgrade upgrade)
    {
        List<upgrade> list = upgradeService.selectupgradeList(upgrade);
        ExcelUtil<upgrade> util = new ExcelUtil<upgrade>(upgrade.class);
        util.exportExcel(response, list, "软件升级数据");
    }

    /**
     * 获取软件升级详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(upgradeService.selectupgradeById(id));
    }

    /**
     * 新增软件升级
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:add')")
    @Log(title = "软件升级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody upgrade upgrade)
    {
        upgrade.setDtUpdate(new Date());
        return toAjax(upgradeService.insertupgrade(upgrade));
    }

    /**
     * 修改软件升级
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:edit')")
    @Log(title = "软件升级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody upgrade upgrade)
    {
        upgrade.setDtUpdate(new Date());
        return toAjax(upgradeService.updateupgrade(upgrade));
    }

    /**
     * 删除软件升级
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:remove')")
    @Log(title = "软件升级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(upgradeService.deleteupgradeByIds(ids));
    }

    /**
     * 升级到指定版本
     */
    @PreAuthorize("@ss.hasPermi('device:upgrade:update')")
    @Log(title = "软件升级", businessType = BusinessType.UPDATE)
	@PutMapping("/upgradeToNew")
    public AjaxResult upgradeToNew(@RequestBody upgrade upgrade)
    {
        // 检测设备是否在线
        DevMsg devMsg = new DevMsg();
        devMsg.setDevName(upgrade.getDevName());
        List<DevMsg> dms = devMsgService.selectDevMsgList(devMsg);
        if (dms.size() > 0) {
            DevMsg dm = devMsgService.selectDevMsgList(devMsg).get(0);
            if (dm.getStatus() != 1) {
                return AjaxResult.success("offline");
            }
        }
        return toAjax(upgradeService.upgradeToNew(upgrade));
    }

}
