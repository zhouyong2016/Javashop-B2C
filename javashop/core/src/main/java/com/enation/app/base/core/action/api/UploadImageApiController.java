package com.enation.app.base.core.action.api;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.Base64ToPicture;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 图片上传API
 * 
 * @author kanon 2015-9-22 version 1.1 添加注释
 */
@Controller
@Scope("prototype")
@RequestMapping("/api/base/upload-image")
public class UploadImageApiController {

	/**
	 * 上传图片
	 * 
	 * @param image
	 *            图片
	 * @param imageFileName
	 *            图片名称
	 * @param subFolder
	 *            存放文件夹名称
	 * @return 上传成功返回： 图片地址，失败返回上传图片错误信息
	 */
	@ResponseBody
	@RequestMapping(value = "/upload-image")
	public Object uploadImage(MultipartFile file, String subFolder) {
		try {
			if (file != null) {
				if (!FileUtil.isAllowUpImg(file.getOriginalFilename())) {
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				} else {
					InputStream stream = null;
					stream = file.getInputStream();
					IUploader uploader = UploadFacatory.getUploaer();
					String fsImgPath = uploader.upload(stream, subFolder, file.getOriginalFilename());
					// return
					// "{\"img\":\""+StaticResourcesUtil.convertToUrl(fsImgPath)+"\",\"fsimg\":\""+fsImgPath+"\"}";
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("img", StaticResourcesUtil.convertToUrl(fsImgPath));
					map.put("fsimg", fsImgPath); 
					/**
					 * IE9以下上传非jpg格式图片出错修复_by jianghongyan
					 * IE9 没出现问题，版本回退至 jsonResultUtil 处理 chopper
					 */ 
					return JsonResultUtil.getObjectJson(map);
				}
			}
		} catch (Exception e) {

			return JsonResultUtil.getErrorJson("上传出错" + e.getLocalizedMessage());
		}
		return JsonResultUtil.getErrorJson("请重试");
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upload-img")
	public String uploadImg(MultipartFile upfile) {
		InputStream stream = null;
		try {
			stream = upfile.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		IUploader uploader = UploadFacatory.getUploaer();
		String path = uploader.upload(stream, "ueditor", upfile.getOriginalFilename());
		// System.out.println(UploadUtil.replacePath(path));

		Map<String, Object> imgMap = new HashMap<String, Object>();
		imgMap.put("state", "SUCCESS");
		imgMap.put("url", StaticResourcesUtil.convertToUrl(path));
		imgMap.put("title", "show.jpg");
		imgMap.put("original", "show.jpg");

		String con = JSONArray.fromObject(imgMap).toString();
		String configJson = con.substring(1, con.length() - 1);

		return configJson;
	}

	/**
	 * 上传图片
	 * 
	 * @param image
	 *            图片
	 * @param imageFileName
	 *            图片名称
	 * @param subFolder
	 *            存放文件夹名称
	 * @return 上传成功返回： 图片地址，失败返回上传图片错误信息
	 */
	@ResponseBody
	@RequestMapping(value = "/upload")
	public Object upload(MultipartFile image, String subFolder) {
		try {
			if (image != null) {
				if (!FileUtil.isAllowUpImg(image.getOriginalFilename())) {
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				} else {
					InputStream stream = null;
					try {
						stream = image.getInputStream();
					} catch (Exception e) {
						e.printStackTrace();
					}
					IUploader uploader = UploadFacatory.getUploaer();
					String fsImgPath = uploader.upload(stream, subFolder, image.getOriginalFilename());
					return "{\"img\":\"" + StaticResourcesUtil.convertToUrl(fsImgPath) + "\",\"fsimg\":\"" + fsImgPath
							+ "\"}";
				}

			}

		} catch (Throwable e) {
			return JsonResultUtil.getErrorJson("上传出错" + e.getLocalizedMessage());
		}
		return JsonResultUtil.getErrorJson("请重试");
	}

	/**
	 *  上传base64图片
	 * @param base64Len 长度 
	 * @param base64	字节码
	 * @param type		类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/upload-base64")
	public Object uploadBase64(int base64Len, String base64,String type) {
		String fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + base64.substring(base64.indexOf("/") + 1, base64.indexOf(";"));
		String path = EopSetting.FILE_STORE_PREFIX + "/attachment/"+ fileName;
		//如果是头像，则保存指定目录
		if(type.equals("avatar")){
			path = EopSetting.FILE_STORE_PREFIX + "/attachment/avatar/"+ fileName;
		}
		
		//图片长度不对等 则返回错误信息
		if(base64.length()!=base64Len){
			return JsonResultUtil.getErrorJson("上传失败，请稍后重试。");
		}
		
		//如果长度超过4m者禁止上传
		if(base64Len>4400000){
			return JsonResultUtil.getErrorJson("图片不符合标准（大于4m），请重新上传。");
		}
		
		// 截取有用的部分
		base64 = base64.substring(base64.indexOf(",") + 1);
		// 如果图片上传成功
		if (Base64ToPicture.GenerateImage(base64, replaceFsToPath(path))) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", StaticResourcesUtil.convertToUrl(path));
			map.put("fsimg", path); 
			return JsonResultUtil.getObjectJson(map);
		} else {
			return JsonResultUtil.getErrorJson("上传失败，请稍后重试");
		}
	}

	
	/**
	 *  替换fs为服务器路径
	 * @param path 路径
	 * @return 返回地址
	 */
	public static String replaceFsToPath(String path){

		if(StringUtil.isEmpty(path)) return path; 
		return     path.replaceAll(EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_path() );
		
	}
	@ResponseBody
	@RequestMapping(value = "/sd", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object dsss() {
		return "asdd";
	}
}
