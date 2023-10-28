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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.FileBusinessType;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.oss.OssUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.device.domain.Version;
import com.ruoyi.device.service.IVersionService;

/**
 * 软件版本Controller
 * 
 * @author cbw
 * @date 2022-11-15
 */
@RestController
@RequestMapping("/device/version")
public class VersionController extends BaseController {
    @Autowired
    private IVersionService VersionService;

    /**
     * 查询软件版本列表
     */
    @PreAuthorize("@ss.hasPermi('device:version:list')")
    @GetMapping("/list")
    public TableDataInfo list(Version Version) {
        System.out.println(Version.toString());
        startPage();
        List<Version> list = VersionService.selectVersionList(Version);
        return getDataTable(list);
    }

    /**
     * 导出软件版本列表
     */
    @PreAuthorize("@ss.hasPermi('device:version:export')")
    @Log(title = "软件版本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Version Version) {
        List<Version> list = VersionService.selectVersionList(Version);
        ExcelUtil<Version> util = new ExcelUtil<Version>(Version.class);
        util.exportExcel(response, list, "软件版本数据");
    }

    /**
     * 获取软件版本详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:version:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(VersionService.selectVersionById(id));
    }

    /**
     * 新增软件版本
     */
    @PreAuthorize("@ss.hasPermi('device:version:add')")
    @Log(title = "软件版本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Version Version) {
        return toAjax(VersionService.insertVersion(Version));
    }

    /**
     * 修改软件版本
     */
    @PreAuthorize("@ss.hasPermi('device:version:edit')")
    @Log(title = "软件版本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Version Version) {
        return toAjax(VersionService.updateVersion(Version));
    }

    /**
     * 删除软件版本
     */
    @PreAuthorize("@ss.hasPermi('device:version:remove')")
    @Log(title = "软件版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(VersionService.deleteVersionByIds(ids));
    }

    /**
     * 上传设备文件
     */
    @PreAuthorize("@ss.hasPermi('device:devMsg:add')")
    @Log(title = "设备文件", businessType = BusinessType.UPLOAD)
    @PostMapping("/uploadVersionFile")
    public AjaxResult uploadversion(MultipartFile[] uploadFile, String versionNum, String name,
            String type) throws Exception {
        int num = 0;
        // 有上传文件就处理，没有就不处理
        if (uploadFile != null) {
            for (MultipartFile file : uploadFile) {
                if (file.isEmpty()) {
                    return toAjax(0);
                }
                // DevMsg devMsg = devMsgService.selectDevMsgById(devName);
                // 获取文件名称
                String fileName = file.getOriginalFilename();
                // 设置文件上传的目录分级
                String prefix = type + "/" + versionNum + "/";
                // 上传文件
                String savePath = OssUtils.uploadToOss(file, prefix);

                // 将文件名，文件路径，文件类型保存至文件表数据库中
                Version version = new Version();
                version.setName(name);
                version.setUrl(savePath);
                version.setVersion(versionNum);
                version.setDtUpdate(new Date());
                if (type.equals("lan")) {
                    version.setType(FileBusinessType.LANGUAGE_FILE);
                } else if (type.equals("up")) {
                    version.setType(FileBusinessType.UP_FILE);
                }

                num = VersionService.insertVersion(version);
            }
        }
        return toAjax(num);
    }

    /**
     * 下载升级文件
     */
    // @PreAuthorize("@ss.hasPermi('device:devMsg:list')")
    @Log(title = "软件升级", businessType = BusinessType.DOWNLOAD)
    @RequestMapping("/downloadVersionFile")
    public void downloadversion(HttpServletResponse response, @RequestParam("filePath") String filePath)
            throws Exception {
        // 获取文件名
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        // 文件下载
        OssUtils.downloadFromOss(response, filePath, fileName);
    }

}
