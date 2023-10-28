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
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.device.domain.DownloadList;
import com.ruoyi.device.service.IDownloadListService;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 下载列表Controller
 * 
 * @author cbw
 * @date 2022-12-09
 */
@RestController
@RequestMapping("/device/DownloadList")
public class DownloadListController extends BaseController {
    @Autowired
    private IDownloadListService downloadListService;

    /**
     * 查询下载列表列表
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:list')")
    @GetMapping("/list")
    public TableDataInfo list(DownloadList downloadList) {
        startPage();
        List<DownloadList> list = downloadListService.selectDownloadListList(downloadList);
        return getDataTable(list);
    }

    /**
     * 查询下载列表列表-byUserId
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:list')")
    @GetMapping("/listByUserId")
    public TableDataInfo listByUserId(DownloadList downloadList) {
        startPage();
        LoginUser loginUser = SecurityUtils.getLoginUser();
        downloadList.setUserid(loginUser.getUserId());
        List<DownloadList> list = downloadListService.selectDownloadListList(downloadList);
        return getDataTable(list);
    }

    /**
     * 导出下载列表列表
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:export')")
    @Log(title = "下载列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DownloadList downloadList) {
        List<DownloadList> list = downloadListService.selectDownloadListList(downloadList);
        ExcelUtil<DownloadList> util = new ExcelUtil<DownloadList>(DownloadList.class);
        util.exportExcel(response, list, "下载列表数据");
    }

    /**
     * 获取下载列表详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(downloadListService.selectDownloadListById(id));
    }

    /**
     * 新增下载列表
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:add')")
    @Log(title = "下载列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DownloadList downloadList) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        downloadList.setUserid(loginUser.getUserId());
        downloadList.setDtUpdate(new Date());
        int num = downloadListService.insertDownloadList(downloadList);
        // if(num > 0){
        //     List<DownloadList> lds = downloadListService.selectDownloadListList(downloadList);
        //     if (lds.size() > 0) {
        //         DownloadList dl = lds.get(0);
        //         // 主要的目的是为了能够将插入之后的id返回给前台，然后前台进行状态更新
        //         return AjaxResult.success(dl.getId());
        //     }
        // }
        return toAjax(num);
    }

    /**
     * 修改下载列表
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:edit')")
    @Log(title = "下载列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DownloadList downloadList) {
        return toAjax(downloadListService.updateDownloadList(downloadList));
    }

    /**
     * 删除下载列表
     */
    @PreAuthorize("@ss.hasPermi('device:DownloadList:remove')")
    @Log(title = "下载列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(downloadListService.deleteDownloadListByIds(ids));
    }
}
