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

import com.ruoyi.business.UserDevMap;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.device.domain.ParamValue;
import com.ruoyi.device.domain.SysSettings;
import com.ruoyi.device.service.IParamValueService;
import com.ruoyi.device.service.ISysSettingsService;

/**
 * 设备参数Controller
 * 
 * @author cbw
 * @date 2022-10-14
 */
@RestController
@RequestMapping("/param/paramValue")
public class ParamValueController extends BaseController {
    @Autowired
    private IParamValueService paramValueService;

    @Autowired
    private ISysSettingsService sysSettingsService;

    /**
     * 查询设备参数列表
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/list")
    public TableDataInfo list(ParamValue paramValue) {
        startPage();
        List<ParamValue> list = paramValueService.selectParamValueList(paramValue);
        return getDataTable(list);
    }

    /**
     * 查询设备参数列表--适用参数修改页面
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/listForParam")
    public AjaxResult listForParam(ParamValue paramValue) {
        return AjaxResult.success(paramValueService.listForParam(paramValue));
    }

    /**
     * 导出设备参数列表
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:export')")
    @Log(title = "设备参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ParamValue paramValue) {
        List<ParamValue> list = paramValueService.selectParamValueList(paramValue);
        ExcelUtil<ParamValue> util = new ExcelUtil<ParamValue>(ParamValue.class);
        util.exportExcel(response, list, "设备参数数据");
    }

    /**
     * 获取设备参数详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(paramValueService.selectParamValueById(id));
    }

    /**
     * 新增设备参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:add')")
    @Log(title = "设备参数", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ParamValue paramValue) {
        return toAjax(paramValueService.insertParamValue(paramValue));
    }

    /**
     * 修改设备参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:edit')")
    @Log(title = "设备参数", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ParamValue paramValue) {
        // 如果paramValue中的paramSubType为8，表示修改的是系统参数，需要同步修改hcms_sys_settings表中的参数值
        if (paramValue.getParamSubType() == 8) {
            System.out.println("修改系统参数" + paramValue.toString());
            // 首先根据设备名称获取设备参数
            SysSettings settings = new SysSettings();
            settings.setDevName(paramValue.getDevName());
            settings.setParamKey(paramValue.getParamKey());
            List<SysSettings> list = sysSettingsService.selectSysSettingsList(settings);
            if (list.size() > 0) {
                SysSettings syssetting = list.get(0);
                syssetting.setDevName(paramValue.getDevName());
                syssetting.setParamKey(paramValue.getParamKey());
                syssetting.setParamValue(paramValue.getParamValue());
                syssetting.setDtUpdate(new Date());
                sysSettingsService.updateSysSettings(syssetting);
            }
        }
        return toAjax(paramValueService.updateParamValue(paramValue));
    }

    /**
     * 删除设备参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:remove')")
    @Log(title = "设备参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(paramValueService.deleteParamValueByIds(ids));
    }

    /**
     * 采集数据
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/collData")
    public AjaxResult collData(ParamValue paramValue) {
        // 检查是否有最近1小时的数据
        List<ParamValue> pvs = paramValueService.selectParamValueList(paramValue);
        if (pvs.size() > 0 && new Date().getTime() - pvs.get(0).getDtUpdate().getTime() < 3600 * 1000) {
            return AjaxResult.success();
        }
        return AjaxResult.success(paramValueService.collData(paramValue));
    }

    /**
     * 采集模块状态数据
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/collModuleStatus")
    public AjaxResult collModuleStatus(ParamValue paramValue) {
        return AjaxResult.success(paramValueService.collModuleStatus(paramValue));
    }

    /**
     * 查询指定斗参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/getDouParam")
    public AjaxResult getDouParam(String devName, Integer param) {
        return paramValueService.getDouParam(devName, param);
    }

    /**
     * 修改指定斗参数
     */
    // @PreAuthorize("@ss.hasPermi('device:paramValue:edit')")
    // @Log(title = "设备参数", businessType = BusinessType.UPDATE)
    // @PutMapping("/setDouParam")
    // public AjaxResult setDouParam(HashMap<String, String> obj)
    // {
    // return paramValueService.setDouParam(obj);
    // }

    /**
     * 查询模块状态参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/getMkParam")
    public AjaxResult getMkParam(String devName, Integer param) {
        return paramValueService.getMkParam(devName, param);
    }

    /**
     * 查询马达模式参数
     * 
     * @throws Exception
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/getMotorParam")
    public AjaxResult getMotorParam(String devName, Integer mainModel, Integer subModel) {
        return paramValueService.getMotorParam(devName, mainModel, subModel);
    }

    /**
     * 修改频率增益参数
     * 
     * @throws Exception
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @Log(title = "设备参数", businessType = BusinessType.UPDATE)
    @PutMapping("/setPlzyParam")
    public AjaxResult setPlzyParam(@RequestBody ParamValue pv) {
        return paramValueService.setPlzyParam(pv);
    }

    /**
     * 查询输入输出参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:list')")
    @GetMapping("/getInOutParam")
    public AjaxResult getInOutParam(String devName, Integer param, Integer inOut) {
        return paramValueService.getInOutParam(devName, param, inOut);
    }

    /**
     * 修改输入输出参数
     */
    @PreAuthorize("@ss.hasPermi('device:paramValue:edit')")
    @Log(title = "设备参数", businessType = BusinessType.UPDATE)
    @PutMapping("/setInOutParam")
    public AjaxResult setInOutParam(@RequestBody ParamValue paramValue) {
        return toAjax(paramValueService.setInOutParam(paramValue));
    }

}
