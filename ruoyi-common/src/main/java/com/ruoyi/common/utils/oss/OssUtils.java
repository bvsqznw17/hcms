package com.ruoyi.common.utils.oss;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.ruoyi.common.utils.spring.CommonProperties;

/**
 * OSS工具类
 * @author cbw
 *
 */
public class OssUtils {
	
	private static String endPoint = CommonProperties.getProperty("aliyun.oss.endPoint");
	
	private static String accessKeyId = CommonProperties.getProperty("aliyun.oss.keyId");
    
	private static String accessKeySecret = CommonProperties.getProperty("aliyun.oss.keySecret");
    
	private static String bucketName = CommonProperties.getProperty("aliyun.oss.bucketName");
    
	/**
	 * 获取阿里云OSS客户端连接
	 * @return
	 */
	public static OSS getOssClient() {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        return ossClient;
	}
	
	/**
	 * 上传文件到OSS
	 * @param file 要上传的文件
	 * @param pathObj 上传文件的路径组成信息
	 * @return savePath 文件保存的地址
	 */
	public static String uploadToOss(MultipartFile file, String prefix) throws IOException {
		
		// 文件预处理
		String fileName = prefix + file.getOriginalFilename();
		String uploadPath = fileName;
		
        // 连接oss客户端
        OSS ossClient = getOssClient();
        
        // 上传文件流
        InputStream inputStream = file.getInputStream();
        
        // 上传
        ossClient.putObject(bucketName, uploadPath, inputStream);
        
        // 关闭连接
        ossClient.shutdown();
        
        return uploadPath;
	}
	
	/**
	 * 上传普通文件到OSS
	 */
	public static String uploadToOss(File file, String uploadPath) throws IOException {
		
        // 连接oss客户端
        OSS ossClient = getOssClient();
        
        // 上传文件流
        InputStream inputStream = new FileInputStream(file);
        
        // 上传
        ossClient.putObject(bucketName, uploadPath, inputStream);
        
        // 关闭连接
        ossClient.shutdown();
        
        return uploadPath;
	}
	
	/**
	 * 从OSS上下载文件
	 */
    public static void downloadFromOss(final HttpServletResponse response, String filePath, String fileName) throws IOException {
    	
        // 连接oss客户端
        OSS ossClient = getOssClient();
        
	// 根据路径校验文件是否存在
    	if (ossClient.doesObjectExist(bucketName, filePath)) {
    		
    		response.setHeader("content-type", "application/octet-stream");
    		response.setContentType("application/octet-stream");
    		
    		// 下载文件能正常显示中文
    		try {
    			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}
    		
    		// 实现文件下载
    		
    		// OSSObject object = ossClient.getObject(bucketName,filePath);
			// GetObjectProgressListener progressListener = new GetObjectProgressListener();
			// OSSObject object = ossClient.getObject(new GetObjectRequest(bucketName, filePath).<GetObjectRequest>withProgressListener(progressListener));
			// response.setHeader("Content-Length", ""+progressListener.getTotalBytes());	
			OSSObject object = ossClient.getObject(bucketName,filePath);
			response.setContentLength(Integer.parseInt(object.getObjectMetadata().getContentLength() + ""));//设置下载文件的长度
    		
    		byte[] buffer = new byte[1024 * 10];
    		InputStream ins = object.getObjectContent();
    		BufferedInputStream bis = null;
    		try {
    			bis = new BufferedInputStream(ins);
    			OutputStream os = response.getOutputStream();
    			int i = bis.read(buffer);
    			while (i != -1) {
    				os.write(buffer, 0, i);
    				i = bis.read(buffer);
					os.flush();
    			}
    			os.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			assert bis != null;
    			bis.close();
    			ins.close();
    			ossClient.shutdown();
    		}
    	}else{
			throw new RuntimeException("文件不存在");
		}
    }
    
    /**
     * 从OSS上下载文件保存到本地
     */
    
    /**
     * 从OSS上打包下载文件
     */
    public static void downloadZipFromOss(List<String> filePaths,HttpServletResponse response) throws Exception{
		response.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=0001.zip");
		BufferedInputStream bis = null;
		try {
			ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
			OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
			for (String filePath : filePaths) {
				if (filePath == null || filePath.equals("")) {
					continue;
				}
				Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
				GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filePath, HttpMethod.GET);
				// 设置过期时间。
				request.setExpiration(expiration);
				// 生成签名URL（HTTP GET请求）。
				URL signedUrl = ossClient.generatePresignedUrl(request);
				// 使用签名URL发送请求。
				OSSObject ossObject = ossClient.getObject(signedUrl, new HashMap<>());

				if (ossObject != null) {
					InputStream inputStream = ossObject.getObjectContent();
					byte[] buffs = new byte[1024 * 10];

					String zipFile = filePath.substring(filePath.lastIndexOf("/") + 1);
					ZipEntry zipEntry = new ZipEntry(zipFile);
					zos.putNextEntry(zipEntry);
					bis = new BufferedInputStream(inputStream, 1024 * 10);

					int read;
					while ((read = bis.read(buffs, 0, 1024 * 10)) != -1) {
						zos.write(buffs, 0, read);
					}
					ossObject.close();
				}
			}
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//关闭流
			try {
				if (null != bis) {
					bis.close();
				}
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
    
	
	/**
	 * 删除OSS上的文件
	 */
	public static void deleteOssFile(String savePath) {
		OSS oss = getOssClient();
		if (null != savePath && oss.doesObjectExist(bucketName, savePath)) {
			oss.deleteObject(bucketName, savePath);
		}
	}
	
	/**
	 * 根据前缀获取bucket的文件列表
	 */
	public static List<String> getOssFileList(String keyPrefix) {
		
		List<String> fileNameList = new ArrayList<String>();
		
		// 创建OSSClient实例。
		OSS ossClient = getOssClient();

		// 列举文件。如果不设置KeyPrefix，则列举存储空间下的所有文件。如果设置KeyPrefix，则列举包含指定前缀的文件。
		ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);
		List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
		for (OSSObjectSummary s : sums) {
			fileNameList.add(s.getKey());
		}

		// 关闭OSSClient。
		ossClient.shutdown();
		
		return fileNameList;
	}
	
	/**
	 * 生成签名url
	 */
	public static String generateSignedUrl(String savePath) {
		OSS oss = getOssClient();

		// 设置签名URL过期时间为3600秒（1小时）。
		Date expiration = new Date(new Date().getTime() + 3600 * 1000);
		// 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
		URL url = oss.generatePresignedUrl(bucketName, savePath, expiration);
		// 关闭OSSClient。
		oss.shutdown(); 
		return url.toString();
	}
	
	/**
	 * 从oss上下载资源并存储到本地
	 */
	public static boolean downloadAndStoreToLocal(String filePath, String localPath) {

		// 创建OSSClient实例。
		OSS ossClient = getOssClient();
		try {
			// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
			// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
			String fileName = filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
			ossClient.getObject(new GetObjectRequest(bucketName, filePath), new File(localPath+fileName));
		} catch (Exception e) {
			return false;
		} finally {
			// 关闭OSSClient。
			ossClient.shutdown();
		}

		return true;
	}

	/**
	 * 从oss上下载ossObject
	 */
	public static OSSObject downloadOssObject(String filePath) {
		// 创建OSSClient实例。
		OSS ossClient = getOssClient();
		try {
			if (ossClient.doesObjectExist(bucketName, filePath)) {
				return ossClient.getObject(bucketName, filePath);
			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			// 关闭OSSClient。
			ossClient.shutdown();
		} 
	}

		/**
	 * 从oss上下载资源到本地文件
	 */
	public static boolean downloadOssFile(String filePath, File file) {

		// 创建OSSClient实例。
		OSS ossClient = getOssClient();
		try {
			// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
			// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
			ossClient.getObject(new GetObjectRequest(bucketName, filePath), file);
		} catch (Exception e) {
			return false;
		} finally {
			// 关闭OSSClient。
			ossClient.shutdown();
		}

		return true;
	}


}
