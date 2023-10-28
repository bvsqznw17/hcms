package com.ruoyi.web.controller.device;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.device.domain.DevAuth;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.service.IDevAuthSevice;
import com.ruoyi.device.service.IDevUserAuthService;

/**
 * 设备授权Controller
 */
@RestController
@RequestMapping("/device/userAuth")
public class DevUserAuthController extends BaseController {

    @Autowired
    private IDevUserAuthService devUserAuthService;

    @Autowired
    private IDevAuthSevice devAuthSevice;

    /**
     * 根据agentId获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/listUserByAgentId")
    public TableDataInfo listUserByAgentId(String agentId) {
        startPage();
        List<SysUser> list = devUserAuthService.listUserByAgentId(agentId);
        return getDataTable(list);
    }

    /**
     * 获取设备树
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/getDevTree")
    public AjaxResult getDevTree(DevAuth devAuth) {
        return devAuthSevice.getDevTree(devAuth);
    }

    /**
     * 根据代理商id获取设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/listDevByAgentId")
    public AjaxResult listDevByAgentId(DevAuth devAuth) {
        return devAuthSevice.listDevByAgentId(devAuth);
    }

    /**
     * 获取代理商树
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/getAgentTree")
    public AjaxResult getAgentTree() {
        return devAuthSevice.getAgentTree();
    }

    /**
     * 获取代理商对于设备的权限
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/listAgentDevAuth")
    public TableDataInfo listAgentDevAuth(DevAuth devAuth) {
        startPage();
        List<DevMsg> list = devAuthSevice.listAgentDevAuth(devAuth);
        return getDataTable(list);
    }

    /**
     * 批量新增文件权限
     */
    @Log(title = "设备授权", businessType = BusinessType.INSERT)
    @PostMapping("/batchAddDevAuth")
    public AjaxResult batchAddDevAuth(@RequestBody List<DevAuth> devAuthList) {
        return toAjax(devAuthSevice.batchAddDevAuth(devAuthList));
    }

    /**
     * 删除代理商对于设备的权限
     */
    @Log(title = "设备授权", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteAuth")
    public AjaxResult deleteAgentDevAuth(String agentId, String devIds) {
        return toAjax(devAuthSevice.deleteAgentDevAuth(agentId, devIds));
    }

}
