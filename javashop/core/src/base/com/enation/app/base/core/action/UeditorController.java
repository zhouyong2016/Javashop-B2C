package com.enation.app.base.core.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：百度富文本 ueditor Action
 *  修改人：xulipeng
 *  修改时间：2015-10-27
 *  修改内容：制定初版
 *  
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/ueditor") 
@SuppressWarnings({ "unused", "rawtypes" })
public class UeditorController {

	
	/**
	 * 百度富文本统一接口
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-config-json")
	public Object getConfigJson(String action){
		
		if(action==null){
			return JsonResultUtil.getErrorJson("出现错误!");
		}
		if(action.equals("config")){	//加载百度富文本 初始化配置。
			return getConfig();
			
		}
		return null;
	}
	
	/**
	 * 初始化配置
	 * @return
	 */
	private String getConfig(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String ctx =request.getContextPath();
		
		Map config = new HashMap();				//总配置对象
		
		/* 上传图片配置项 */
		config.put("imageActionName", "uploadimage");
		config.put("imageUrl", ctx+"/core/admin/ueditor/upload-img.do");
		config.put("imageFieldName", "upfile");
		config.put("imageMaxSize", "2048000");
		config.put("imageUrlPrefix", ctx);
		config.put("imageCompressEnable", false);
		config.put("imageCompressBorder", 1600);
		config.put("imageInsertAlign", "none");
		config.put("imageUrlPrefix", "");
		
		String [] imageFormat = new String[5];	//图片上传格式配置
		imageFormat[0] = ".png";
		imageFormat[1] = ".jpg";
		imageFormat[2] = ".jpeg";
		imageFormat[3] = ".gif";
		imageFormat[4] = ".bmp";
		
		config.put("imageAllowFiles", imageFormat);
		config.put("imagePathFormat", "/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}");
		
		/* 涂鸦图片上传配置项 */
		
		/* 抓取远程图片配置 */
		
		String con = JSONArray.fromObject(config).toString();
	    String configJson = con.substring(1, con.length()-1);
	    
		return configJson;
	}
	
	/**
	 * 上传图片
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/upload-img")
	public  String uploadImg(MultipartFile upfile){
		InputStream stream=null;
		try {
			stream=upfile.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		IUploader uploader=UploadFacatory.getUploaer();
		String path = uploader.upload(stream, "ueditor",upfile.getOriginalFilename());
		//System.out.println(UploadUtil.replacePath(path));
		
		Map imgMap = new HashMap();
		imgMap.put("state", "SUCCESS");
		imgMap.put("url", StaticResourcesUtil.convertToUrl(path));
		imgMap.put("title", "show.jpg");
		imgMap.put("original", "show.jpg");
		
		String con = JSONArray.fromObject(imgMap).toString();
	    String configJson = con.substring(1, con.length()-1);
		
		return configJson;
	}

	
}
