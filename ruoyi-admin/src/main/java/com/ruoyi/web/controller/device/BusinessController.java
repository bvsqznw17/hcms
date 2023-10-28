package com.ruoyi.web.controller.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.device.service.IBusinessService;

@RestController
@RequestMapping("/device/business")
public class BusinessController extends BaseController {

    @Autowired
    private IBusinessService businessService;

    /**
     * 检测设备是否可以被操作
     * 
     * @param devAuth
     * @return
     */
    @GetMapping("/checkDevStatus")
    public AjaxResult checkDevStatus(String devName) {
        String userId = getLoginUser().getUser().getUserId().toString();
        return businessService.checkDevStatus(devName, userId);
    }

    /**
     * 更改设备的操作状态
     */
    @GetMapping("/changeDevStatus")
    public AjaxResult changeDevStatus(String devName, String operateStatus) {
        return toAjax(businessService.changeDevStatus(devName, operateStatus));
    }

    /**
     * 给设备发送指令
     */
    @GetMapping("/writeCmd")
    public AjaxResult writeCmd(String devName, String cmd, String cmdParam) {
        return toAjax(businessService.writeCmd(devName, cmd, cmdParam));
    }

    /**
     * 读取设备的参数
     */
    @GetMapping("/readParam")
    public AjaxResult readParam(String devName, String paramKey) {
        return businessService.readParam(devName, paramKey);
    }

    /**
     * 获取设备的运行操作状态
     */
    @GetMapping("/getRunStatus")
    public AjaxResult getRunStatus(String devName) {
        return businessService.getRunStatus(devName);
    }

    /**
     * 获取组合面板的数据
     */
    @GetMapping("/getPanelData")
    public AjaxResult getPanelData(String devName) {
        return businessService.getPanelData(devName);
    }

    /**
     * 获取80个short的斗状态数据
     */
    @GetMapping("/getDouStatus")
    public AjaxResult getDouStatus(String devName) {
        return businessService.getDouStatus(devName);
    }

}
