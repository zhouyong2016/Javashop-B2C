package com.enation.app.base.core.action.api;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
@Controller
@Scope("prototype")
@RequestMapping("/api/base/tagTestCreate")
public class TagTestCreateController  {
	
	@ResponseBody
	@RequestMapping
	public Object execute(String content,String params,String filename){
		try{
			if(StringUtil.isEmpty(filename)){
				filename = createFileName()+".html";
			}
			String app_apth = StringUtil.getRootPath();
			String filepath=app_apth+"/docs/tags/runtime/"+filename;
			if(content==null){
				content="";
			}
			//安全性校验
			this.filterAttrack(content);
			FileUtil.write(filepath, content);
			if(params==null){
				params="";
			}
			return JsonMessageUtil.getStringJson("url", filename);
		}catch(Throwable e){
			Logger logger = Logger.getLogger(getClass());
			logger.error("生成标签测试页面出错",e);
			return JsonResultUtil.getErrorJson("生成标签测试页面出错:"+e.getMessage());
		}
	}
	/**
	 * 安全性校验过滤
	 * @param content
	 */
	private void filterAttrack(String content) {
		// TODO Auto-generated method stub
		if(content.indexOf("new()")>-1){
			throw new RuntimeException("不乖了哦，不允许远程执行new()代码");
		}
	}
	
	private String createFileName(){
		String filename = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
		
		return filename+StringUtil.getRandStr(4);
	} 
	
	
}
