package com.enation.framework.directive;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 图片指令
 * @author kingapex
 *2012-3-25上午8:45:37
 */
public class ImageDirectiveModel implements TemplateDirectiveModel{
	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		 
		String src = params.get("src").toString();
		String postfix= this.getValue(params, "postfix");
		String imageurl = getImageUrl(src,postfix);
		StringBuffer html = new StringBuffer();
		
		html.append("<img");
		html.append(" src=\""+imageurl+"\"");
		
		
		Set keySet = params.keySet();
		Iterator<String> itor = keySet.iterator();
		
		while(itor.hasNext()){
			String name = itor.next();
			if("src".equals(name)){ continue; }
			if("postfix".equals(name)){ continue; }
			String value =this.getValue(params, name);
			if(!StringUtil.isEmpty(value)){
				html.append(" "+name+"=\""+value+"\"");
			}
		}
		
		
 
		
		html.append(" />");
		env.getOut().write(html.toString());
	}
	
	
	protected String getValue(Map params, String name) {

		Object value_obj = params.get(name);
		if (value_obj == null) {
			return null;
		}

		return value_obj.toString();
	}
	
	private static String getImageUrl(String pic,String postfix){
		if (StringUtil.isEmpty(pic))
			pic = SystemSetting.getDefault_img_url();
		
		
		//由王峰去掉，为什么要加这个限制呢？如果有如此需求呢：
		//显示这个地址：http://www.abc.com/a.jpg为：http://www.abc.com/a_thumbail.jpg
		//if(pic.toUpperCase().startsWith("HTTP"))//lzf add 20120321
		//	return pic;
		if (pic.startsWith("fs:")) {//静态资源式分离式存储
			pic = StaticResourcesUtil.convertToUrl(pic);
		}
		if (!StringUtil.isEmpty(postfix )) {
			return StaticResourcesUtil.getThumbPath(pic, postfix);
		} else {
			return pic;
		}
	}
	
	public static void main(String args[]){
	}
}
