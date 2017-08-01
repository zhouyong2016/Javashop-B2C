package com.enation.framework.directive;

import java.util.HashMap;
import java.util.Map;

import com.enation.framework.pager.AjaxPagerDirectiveModel;
import com.enation.framework.pager.PagerDirectiveModel;

import freemarker.template.TemplateDirectiveModel;

/**
 * 指令工厂
 * @author kingapex
 *2012-3-24下午10:15:11
 */
public class DirectiveFactory {
	
	
	private DirectiveFactory(){}
	private static Map<String,TemplateDirectiveModel> directiveMap;
	
	public static Map<String,TemplateDirectiveModel> getCommonDirective(){
		
		if(directiveMap==null){
			
			directiveMap= new  HashMap<String, TemplateDirectiveModel>(9);
			
			
			/**
			 * 日期格式化指令
			 */
			TemplateDirectiveModel dateformate = new DateformateDirective();
			directiveMap.put("dateformat", dateformate);
			
			
  
			
			/**
			 * 图片声明指令
			 */
			TemplateDirectiveModel image = new ImageDirectiveModel();
			directiveMap.put("image", image);
			
			
			/**
			 * 异步分页指令
			 */
			TemplateDirectiveModel ajaxpager  = new AjaxPagerDirectiveModel();
			directiveMap.put("ajaxpager", ajaxpager);
			
			
			
			/**
			 * 分页指令
			 */
			TemplateDirectiveModel pager = new PagerDirectiveModel();
			directiveMap.put("pager", pager);
			
			
			/**
			 * 图片url输出指令
			 */
			TemplateDirectiveModel imgurl= new ImageUrlDirectiveModel();
			directiveMap.put("imgurl", imgurl);
			
			
			
			/**
			 * 字串截取指令
			 */
			TemplateDirectiveModel substring = new  SubStringDirectiveModel();
			directiveMap.put("substring", substring);
			
			
			/**
			 * 上传图片指令
			 */
			TemplateDirectiveModel imageUploader = new  ImageUploaderDirectiveModel();
			directiveMap.put("imageuploader", imageUploader);
			
			
			/**
			 * 外链 内链 指令
			 */
			TemplateDirectiveModel link = new  LinkDirectiveModel();
			directiveMap.put("link", link);
			
		 
		}
		
		return directiveMap;
	}
	
}
