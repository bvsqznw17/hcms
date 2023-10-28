package com.ruoyi.device.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.oss.OssUtils;
import com.ruoyi.device.domain.DevFile;
import com.ruoyi.device.domain.DevMsg;
import com.ruoyi.device.mapper.DevFileMapper;
import com.ruoyi.device.mapper.DevMsgMapper;
import com.ruoyi.device.service.IDevFileService;

/**
 * 设备文件Service业务层处理
 * 
 * @author admin
 * @date 2021-09-14
 */
@Service
public class DevFileServiceImpl implements IDevFileService 
{
    @Autowired
    private DevFileMapper devFileMapper;
    
    @Autowired
    private DevMsgMapper devMsgMapper;

    /**
     * 查询设备文件
     * 
     * @param id 设备文件ID
     * @return 设备文件
     */
    @Override
    public DevFile selectDevFileById(Long id)
    {
        return devFileMapper.selectDevFileById(id);
    }

    /**
     * 查询设备文件列表
     * 
     * @param devFile 设备文件
     * @return 设备文件
     */
    @Override
    public List<DevFile> selectDevFileList(DevFile devFile)
    {
        return devFileMapper.selectDevFileList(devFile);
    }

    /**
     * 新增设备文件
     * 
     * @param devFile 设备文件
     * @return 结果
     */
    @Override
    public int insertDevFile(DevFile devFile)
    {
    	if (devFile.getUploadTime() == null) {
    		devFile.setUploadTime(new Date());
		}
        return devFileMapper.insertDevFile(devFile);
    }

    /**
     * 修改设备文件
     * 
     * @param devFile 设备文件
     * @return 结果
     */
    @Override
    public int updateDevFile(DevFile devFile)
    {
        return devFileMapper.updateDevFile(devFile);
    }

    /**
     * 批量删除设备文件
     * 
     * @param ids 需要删除的设备文件ID
     * @return 结果
     */
    @Override
    public int deleteDevFileByIds(Long[] ids)
    {
    	// 根据id获取devFile，根据filePath删除文件
    	List<DevFile> devFiles = devFileMapper.selectDevFileByIds(ids);
    	for(DevFile devFile : devFiles) {
    		OssUtils.deleteOssFile(devFile.getFilePath());
    	}
        return devFileMapper.deleteDevFileByIds(ids);
    }

    /**
     * 删除设备文件信息
     * 
     * @param id 设备文件ID
     * @return 结果
     */
    @Override
    public int deleteDevFileById(Long id)
    {
        return devFileMapper.deleteDevFileById(id);
    }

    /**
     * TODO 通过devCode、businessType、fileType获取文件
     */
	@Override
	public List<String> getDevFilePaths(String devCode, String businessType, String fileType) {
		List<String> paths = new ArrayList<String>();
		DevFile file = new DevFile();
		file.setDevName(devCode);
		file.setBusinessType(businessType);
		file.setFileType(fileType);
		List<DevFile> devFiles = devFileMapper.selectDevFileList(file);
		for(DevFile devFile : devFiles) {
			paths.add(devFile.getFilePath());
		}
		return paths;
	}

	@Override
	public DevFile selectDevFileByDevCode(String devName) {
		return devFileMapper.selectDevFileByDevName(devName);
	}

	/**
	 * TODO 返回页面需要展示的设备文件数据
	 */
	@Override
	public List<HashMap<String, Object>> selectDevFilePageList(DevFile devFile) {
		// 首先通过查询得到所有的设备编号
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		DevMsg devMsg = new DevMsg();
		// devMsg.setDevCode(devFile.getDevCode());
		// List<DevMsg> devMsgs = devMsgMapper.selectDevMsgList(devMsg);
		// // 然后将其组成一个List，通过再次查询，匹配devCode和devFile，组成一行
		// List<DevFile> devFiles = devFileMapper.selectDevFilePageList(devFile);
		// for (DevMsg msg : devMsgs) {
		// 	HashMap<String, Object> map = new HashMap<String, Object>();
		// 	map.put("devCode", msg.getDevCode());
		// 	for(DevFile devFile_ : devFiles) {
		// 		if (devFile_.getDevCode().equals(msg.getDevCode())) {
		// 			if (devFile_.getBusinessType().equals(FileBusinessType.FIRMWARE)) {
		// 				map.put("firmwareFileName", devFile_.getFileName());
		// 				map.put("firmwareFilePath", devFile_.getFilePath());
		// 			}else if (devFile_.getBusinessType().equals(FileBusinessType.LICENCE_FILE)) {
		// 				map.put("licenceFileName", devFile_.getFileName());
		// 				map.put("licenceFilePath", devFile_.getFilePath());
		// 			}else if (devFile_.getBusinessType().equals(FileBusinessType.FACTORY_PARAM)) {
		// 				map.put("factoryParamFileName", devFile_.getFileName());
		// 				map.put("factoryParamFilePath", devFile_.getFilePath());
		// 			}else if (devFile_.getBusinessType().equals(FileBusinessType.CONFIG_FILE)) {
		// 				map.put("configFileName", devFile_.getFileName());
		// 				map.put("configFilePath", devFile_.getFilePath());
		// 			}
		// 		}
		// 	}
		// 	list.add(map);
		// }
		
		return list;
	}

	@Override
	public List<DevFile> getDevFileByKey(String devCode, String keyWords) {
		DevFile devFile = new DevFile();
		devFile.setDevName(devCode);
		devFile.getParams().put("keyWords", keyWords);
		return devFileMapper.getDevFileByKey(devFile);
	}

	@Override
	public void batchDownloadDevFile(HttpServletResponse resp, Long[] ids) {
	   List<DevFile> devFiles = devFileMapper.selectDevFileByIds(ids);
	   List<String> filePaths = new ArrayList<String>();
	   try {
		   for(DevFile devFile : devFiles) {
			   filePaths.add(devFile.getFilePath());
		   }
		   OssUtils.downloadZipFromOss(filePaths, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
