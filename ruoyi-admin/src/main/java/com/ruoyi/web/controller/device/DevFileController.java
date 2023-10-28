package com.ruoyi.web.controller.device;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.FileBusinessType;
import com.ruoyi.common.constant.FileType;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.oss.OssUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.device.domain.DevFile;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.service.IDevFileService;
import com.ruoyi.device.service.IDevMsgService;

import cn.hutool.core.thread.ThreadUtil;

/**
 * 设备文件Controller
 * 
 * @author admin
 * @date 2021-09-14
 */
@RestController
@RequestMapping("/device/devFile")
public class DevFileController extends BaseController {
	@Autowired
	private IDevFileService devFileService;

	@Autowired
	private IDevMsgService devMsgService;

	/**
	 * 查询设备文件列表
	 */
	// @PreAuthorize("@ss.hasPermi('device:devFile:list')")
	@GetMapping("/list")
	public TableDataInfo list(DevFile devFile) {
		startPage();
		List<DevFile> list = devFileService.selectDevFileList(devFile);
		return getDataTable(list);
	}

	/**
	 * 查询设备文件页面展示的列表
	 */
	// @PreAuthorize("@ss.hasPermi('device:devFile:list')")
	@GetMapping("/getDevFilePageList")
	public TableDataInfo getDevFilePageList(DevFile devFile) {
		startPage();
		List<HashMap<String, Object>> list = devFileService.selectDevFilePageList(devFile);

		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(HttpStatus.SUCCESS);
		rspData.setMsg("查询成功");
		rspData.setRows(list);
		DevMsg devMsg = new DevMsg();
		devMsg.setDevName(devFile.getDevName());
		rspData.setTotal(new PageInfo(devMsgService.selectDevMsgList(devMsg)).getTotal());
		return rspData;
	}

	/**
	 * 导出设备文件列表
	 */
	@PreAuthorize("@ss.hasPermi('device:devFile:export')")
	@Log(title = "设备文件", businessType = BusinessType.EXPORT)
	@GetMapping("/export")
	public AjaxResult export(DevFile devFile) {
		List<DevFile> list = devFileService.selectDevFileList(devFile);
		ExcelUtil<DevFile> util = new ExcelUtil<DevFile>(DevFile.class);
		return util.exportExcel(list, "设备文件数据");
	}

	/**
	 * 获取设备文件详细信息
	 */
	@PreAuthorize("@ss.hasPermi('device:devFile:query')")
	@GetMapping(value = "/{id}")
	public AjaxResult getInfo(@PathVariable("id") Long id) {
		return AjaxResult.success(devFileService.selectDevFileById(id));
	}

	/**
	 * 新增设备文件
	 */
	@PreAuthorize("@ss.hasPermi('device:devFile:add')")
	@Log(title = "设备文件", businessType = BusinessType.INSERT)
	@PostMapping
	public AjaxResult add(@RequestBody DevFile devFile) {
		return toAjax(devFileService.insertDevFile(devFile));
	}

	/**
	 * 修改设备文件
	 */
	@PreAuthorize("@ss.hasPermi('device:devFile:edit')")
	@Log(title = "设备文件", businessType = BusinessType.UPDATE)
	@PutMapping
	public AjaxResult edit(@RequestBody DevFile devFile) {
		return toAjax(devFileService.updateDevFile(devFile));
	}

	/**
	 * 删除设备文件
	 */
	@PreAuthorize("@ss.hasPermi('device:devFile:remove')")
	@Log(title = "设备文件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
	public AjaxResult remove(@PathVariable Long[] ids) {
		return toAjax(devFileService.deleteDevFileByIds(ids));
	}

	/**
	 * 下载设备文件
	 */
	// @PreAuthorize("@ss.hasPermi('device:devMsg:list')")
	@Log(title = "设备文件", businessType = BusinessType.DOWNLOAD)
	@RequestMapping("/downloadDevFile")
	public void downloadDevFile(HttpServletResponse response, @RequestParam("filePath") String filePath)
			throws Exception {
		// 获取文件名
		String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
		// 文件下载
		OssUtils.downloadFromOss(response, filePath, fileName);
	}

	/**
	 * 批量下载设备文件
	 */
	@Log(title = "设备文件", businessType = BusinessType.DOWNLOAD)
	@GetMapping("/batchDownloadDevFile/{ids}")
	public void batchDownloadDevFile(HttpServletResponse response, @PathVariable("ids") Long[] ids) throws Exception {
		devFileService.batchDownloadDevFile(response, ids);
	}

	/**
	 * 获取预览的urlList
	 */
	@GetMapping("/getUrlList")
	public AjaxResult getUrlList(DevFile devFile) throws Exception {
		List<HashMap<String, Object>> urlList = new ArrayList<HashMap<String, Object>>();
		// 根据devCode以及文件类型查询devFile
		List<DevFile> devFiles = devFileService.selectDevFileList(devFile);
		// 生成并返回urlList
		String preFileName = "";
		int n = 1;
		for (DevFile devFile_ : devFiles) {
			String fileName = devFile_.getFileName().substring(0, devFile_.getFileName().lastIndexOf('-'));
			String title = fileName;
			// String url = OssUtils.generateSignedUrl(devFile_.getFilePath());
			ThreadUtil.execAsync(
					() -> OssUtils.downloadAndStoreToLocal(devFile_.getFilePath(), RuoYiConfig.getTmpFile()));
			String url = "/tmpFile/" + devFile_.getFileName();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", url);
			map.put("fileType", devFile_.getFileType());
			if (preFileName.equals(fileName)) {
				if (n == 1) {
					// 将之前已经插入的值改为fileName-1形式
					HashMap<String, Object> tmpMap = urlList.get(urlList.size() - 1);
					tmpMap.put("title", title + "-" + 1);
					urlList.set(urlList.size() - 1, tmpMap);
				}
				n++;
				title = fileName + n;
			} else {
				n = 1;
			}
			map.put("title", title);
			preFileName = fileName;
			urlList.add(map);
		}
		Thread.sleep(1000);
		return AjaxResult.success(urlList);
	}

	/**
	 * 上传设备文件
	 */
	// @PreAuthorize("@ss.hasPermi('device:devMsg:add')")
	@Log(title = "设备文件", businessType = BusinessType.UPLOAD)
	@PostMapping("/uploadDevFile")
	public AjaxResult uploadDevFile(MultipartFile[] uploadFile, String devModel, String type) throws Exception {
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
				String prefix = type + "/" + devModel + "/";
				// 上传文件
				String savePath = OssUtils.uploadToOss(file, prefix);

				// 将文件名，文件路径，文件类型保存至文件表数据库中
				DevFile devFile = new DevFile();
				devFile.setFileName(fileName);
				devFile.setFilePath(savePath);
				// devFile.setDevName(devName);
				devFile.setDevModel(devModel);
				devFile.setFileType(FileType.NORMAL);
				if (type.equals("languagePack")) {
					devFile.setBusinessType(FileBusinessType.LANGUAGE_PACK);
				} else if (type.equals("machineInstruction")) {
					devFile.setBusinessType(FileBusinessType.MACHINE_INSTRUCTION);
				} else if (type.equals("partsList")) {
					devFile.setBusinessType(FileBusinessType.PARTS_LIST);
				} else if (type.equals("partsPic")) {
					devFile.setBusinessType(FileBusinessType.PARTS_PIC);
				}

				num = devFileService.insertDevFile(devFile);
			}
		}
		return toAjax(num);
	}

	/**
     * 根据filePath获取文件下载url
     */
	@Log(title = "设备文件", businessType = BusinessType.DOWNLOAD)
	@GetMapping("/getDownloadUrl")
    public AjaxResult getPreviewPicUrlList(String filePath) throws Exception
    {
		String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
		ThreadUtil.execAsync(() -> OssUtils.downloadAndStoreToLocal(filePath, RuoYiConfig.getTmpFile()));
		String url = "/tmpFile/"+fileName;
    	Thread.sleep(1000);
    	return AjaxResult.success(url);
    }

	/**
	 * 业务类型转译器【临时】
	 */
	// public String transFileBusinessType(String fileBusinessType) {
	// HashMap<String, String> map = new HashMap<String, String>();

	// map.put(FileBusinessType.AFTER_REPAIR, "维修后照片");
	// map.put(FileBusinessType.BEFORE_REPAIR, "维修前照片");
	// map.put(FileBusinessType.REPAIR_FORM, "维修报告单");
	// map.put(FileBusinessType.BACK_REPAIR_FORM, "设备返修单");

	// map.put(FileBusinessType.PARTS_PIC, "发货-配件拍照");
	// map.put(FileBusinessType.GROUPED_DEV_PIC, "发货-配套设备照片");
	// map.put(FileBusinessType.BOXING_VIDEO, "发货-装箱视频");
	// map.put(FileBusinessType.SEALING_PIC, "发货-封箱拍照");
	// map.put(FileBusinessType.LOADING_VIDEO, "发货-上车视频");

	// map.put(FileBusinessType.DELIVERY_FORM, "设备验收单");
	// map.put(FileBusinessType.UNPACK_PROCESS, "拆机过程照片");
	// map.put(FileBusinessType.PICK_RESULT, "贴装效果照片");
	// map.put(FileBusinessType.DEBUG_RESULT, "调试结果照片");

	// map.put(FileBusinessType.PICK_VIDEO_IN, "入库贴装视频");
	// map.put(FileBusinessType.PICK_VIDEO_OUT, "出库贴装视频");

	// return map.get(fileBusinessType);
	// }

}
